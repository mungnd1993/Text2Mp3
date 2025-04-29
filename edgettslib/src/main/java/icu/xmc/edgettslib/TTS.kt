package icu.xmc.edgettslib

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import icu.xmc.edgettslib.entity.VoiceItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.Headers.Companion.toHeaders
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class TTS private constructor(){

    companion object{
        private var sInstance: TTS? = null
            get() {
                if (field == null) {
                    field = TTS()
                }
                return field
            }

        fun getInstance(): TTS{
            return sInstance!!
        }
    }

    private val voiceList = arrayListOf<VoiceItem>()

    private var headers: HashMap<String, String>? = null
    private var voice: VoiceItem? = null
    private var format = "audio-24khz-48kbitrate-mono-mp3"
    private var findHeadHook = false
    private var voicePitch = "+0Hz"
    private var voiceRate = "+0%"
    private var voiceVolume = "+0%"
    private var storage = ""
    private var cacheStorage = ""
    private var mediaPlayer: MediaPlayer? = null
    private var lastPlayMp3:File ?= null

    private var request:Request? =null

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    fun initialize(context: Context,voice:VoiceItem){
        this.voice = voice
        if (voiceList.isEmpty()){
            voiceList.addAll(TTSVoice(context).getVoiceList())
        }
        storage = context.filesDir.absolutePath
        cacheStorage = File(context.cacheDir, "TTS").absolutePath
        val cacheDirTTS = File(cacheStorage)
        if (!cacheDirTTS.exists()) {
            cacheDirTTS.mkdirs()
        }
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setOnCompletionListener {
            lastPlayMp3?.delete()
        }
    }

    fun setVoice(voice: VoiceItem): TTS {
        this.voice = voice
        return this
    }

    fun setVoicePitch(voicePitch: Int): TTS {
        this.voicePitch = "+${voicePitch}Hz"
        return this
    }

    fun setVoiceRate(voiceRate: Int): TTS {
        this.voiceRate = "+${voiceRate}%"
        return this
    }

    fun setVoiceVolume(voiceVolume: Int): TTS {
        this.voiceVolume = "+${voiceVolume}%"
        return this
    }

    fun formatMp3(): TTS {
        format = "audio-24khz-48kbitrate-mono-mp3"
        return this
    }

//    fun formatOpus(): TTS {
//        format = "webm-24khz-16bit-mono-opus"
//        return this
//    }

    fun findHeadHook(): TTS {
        findHeadHook = true
        return this
    }

    fun fixHeadHook(): TTS {
        findHeadHook = false
        return this
    }

    fun storage(storage: String): TTS {
        this.storage = storage
        return this
    }

    fun cacheStorage(storage: String): TTS {
        this.cacheStorage = storage
        return this
    }

    fun headers(headers: HashMap<String, String>): TTS {
        this.headers = headers
        return this
    }

    suspend fun speak(content: String): String {
        if (voice == null) {
            throw RuntimeException("please set voice")
        }

        val str = removeIncompatibleCharacters(content)
        if (str.isNullOrBlank()) {
            throw RuntimeException("invalid content")
        }

        val storageFolder = File(cacheStorage)
        if (!storageFolder.exists()) {
            storageFolder.mkdirs()
        }

        val dateStr = dateToString(Date())
        val reqId = uuid()
        val audioFormat = TTSUtil.mkAudioFormat(dateStr, format)
        val ssml = TTSUtil.mkssml(voice!!.Locale, voice!!.Name, content, voicePitch, voiceRate, voiceVolume)
        val ssmlHeadersPlusData = TTSUtil.ssmlHeadersPlusData(reqId, dateStr, ssml)

        if (headers == null) {
            headers = HashMap()
            headers?.put("Origin", UrlConstant.EDGE_ORIGIN)
            headers?.put("Pragma", "no-cache")
            headers?.put("Cache-Control", "no-cache")
            headers?.put("User-Agent", UrlConstant.EDGE_UA)
        }

        var fileName = reqId
        if (format == "audio-24khz-48kbitrate-mono-mp3") {
            fileName += ".mp3"
        } else if (format == "webm-24khz-16bit-mono-opus") {
            fileName += ".opus"
        }

        val storageFile = File(cacheStorage)
        if (!storageFile.exists()) {
            storageFile.mkdirs()
        }

        if (request == null) {
            request = Request.Builder()
                .url(UrlConstant.EDGE_URL)
                .headers(headers!!.toHeaders())
                .build()
        }

        return withContext(Dispatchers.IO) {
            try {
                val result = suspendCancellableCoroutine<String> { continuation ->
                    val listener = TTSWebSocketListener(cacheStorage, fileName, findHeadHook)

                    // Đặt callback khi file hoàn tất (nếu bạn đã thêm callback vào TTSWebSocketListener)
                    if (listener is TTSWebSocketListener) {
                        try {
                            val setCallbackMethod = listener.javaClass.getMethod("setFileCompleteCallback", TTSWebSocketListener.FileCompleteCallback::class.java)
                            val callbackInstance = object : TTSWebSocketListener.FileCompleteCallback {
                                override fun onFileComplete(filePath: String) {
                                    Log.d("TTS", "File completed in speak: $filePath")
                                    continuation.resume(filePath)
                                }
                            }
                            setCallbackMethod.invoke(listener, callbackInstance)
                        } catch (e: Exception) {
                            Log.e("TTS", "Failed to set callback: ${e.message}")
                        }
                    }

                    val client = okHttpClient.newWebSocket(request!!, listener)

                    client.send(audioFormat)
                    client.send(ssmlHeadersPlusData)

                    // Kiểm tra file định kỳ để đảm bảo không bỏ lỡ
                    val checkJob = GlobalScope.launch {
                        var attempts = 0
                        val maxAttempts = 50 // Kiểm tra trong 15 giây (50 x 300ms)

                        while (attempts < maxAttempts) {
                            val checkFile = File(cacheStorage, fileName)

                            if (checkFile.exists() && checkFile.length() > 0) {
                                if (continuation.isActive) {
                                    Log.d("TTS", "File check success in speak: ${checkFile.absolutePath}")
                                    continuation.resume(checkFile.absolutePath)
                                }
                                return@launch
                            }

                            delay(300)
                            attempts++
                        }

                        // Nếu hết thời gian mà vẫn không tìm thấy file
                        if (continuation.isActive) {
                            continuation.resumeWithException(RuntimeException("File not created after $maxAttempts attempts"))
                        }
                    }

                    continuation.invokeOnCancellation {
                        checkJob.cancel()
                        client.cancel()
                    }
                }
                result
            } catch (e: Throwable) {
                e.printStackTrace()
                throw e
            }
        }
    }

    fun speakAndSave(content: String) {
        if (voice == null) {
            throw RuntimeException("please set voice")
        }
        val str = removeIncompatibleCharacters(content)
        if (str.isNullOrBlank()) {
            throw RuntimeException("invalid content")
        }
        val storageFolder = File(storage)
        if (!storageFolder.exists()) {
            storageFolder.mkdirs()
        }
        val dateStr = dateToString(Date())
        val reqId = uuid()
        val audioFormat = TTSUtil.mkAudioFormat(dateStr,format)
        val ssml = TTSUtil.mkssml(voice!!.Locale, voice!!.Name,content,voicePitch,voiceRate,voiceVolume)
        val ssmlHeadersPlusData = TTSUtil.ssmlHeadersPlusData(reqId, dateStr, ssml)
        if (headers == null) {
            headers = HashMap()
            headers?.put("Origin", UrlConstant.EDGE_ORIGIN)
            headers?.put("Pragma", "no-cache")
            headers?.put("Cache-Control", "no-cache")
            headers?.put("User-Agent", UrlConstant.EDGE_UA)
        }
        var fileName = reqId
        if (format == "audio-24khz-48kbitrate-mono-mp3") {
            fileName += ".mp3"
        } else if (format == "webm-24khz-16bit-mono-opus") {
            fileName += ".opus"
        }
        val storageFile = File(storage)
        if (!storageFile.exists()){
            storageFile.mkdirs()
        }
        if (request == null){
            request = Request.Builder()
                .url(UrlConstant.EDGE_URL)
                .headers(headers!!.toHeaders())
                .build()
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client =
                    okHttpClient.newWebSocket(request!!, object : TTSWebSocketListener(storage, fileName, findHeadHook) {
                        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                            val file = File(storage,fileName)
                            if (file.exists()){
                                mediaPlayer?.reset()
                                mediaPlayer?.setDataSource(file.absolutePath)
                                mediaPlayer?.prepare()
                                mediaPlayer?.start()
                            }
                        }

                    })
                client.send(audioFormat)
                client.send(ssmlHeadersPlusData)
            }catch (e:Throwable){
                e.printStackTrace()
            }
        }
    }

    suspend fun save(content: String): String {
        if (voice == null) {
            throw RuntimeException("please set voice")
        }

        val str = removeIncompatibleCharacters(content)
        if (str.isNullOrBlank()) {
            throw RuntimeException("invalid content")
        }

        val dateStr = dateToString(Date())
        val reqId = uuid()
        val audioFormat = TTSUtil.mkAudioFormat(dateStr, format)
        val ssml = TTSUtil.mkssml(voice!!.Locale, voice!!.Name, content, voicePitch, voiceRate, voiceVolume)
        val ssmlHeadersPlusData = TTSUtil.ssmlHeadersPlusData(reqId, dateStr, ssml)

        if (headers == null) {
            headers = HashMap()
            headers?.put("Origin", UrlConstant.EDGE_ORIGIN)
            headers?.put("Pragma", "no-cache")
            headers?.put("Cache-Control", "no-cache")
            headers?.put("User-Agent", UrlConstant.EDGE_UA)
        }

        var fileName = reqId
        if (format == "audio-24khz-48kbitrate-mono-mp3") {
            fileName += ".mp3"
        } else if (format == "webm-24khz-16bit-mono-opus") {
            fileName += ".opus"
        }

        val file = File(storage, fileName)
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }

        val currentRequest = Request.Builder()
            .url(UrlConstant.EDGE_URL)
            .headers(headers!!.toHeaders())
            .build()

        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                val listener = TTSWebSocketListener(storage, fileName, findHeadHook)

                // Đặt callback khi file hoàn tất
                listener.setFileCompleteCallback(object : TTSWebSocketListener.FileCompleteCallback {
                    override fun onFileComplete(filePath: String) {
                        Log.d("TTS", "File completed: $filePath")
                        continuation.resume(filePath)
                    }
                })

                val client = okHttpClient.newWebSocket(currentRequest, listener)

                client.send(audioFormat)
                client.send(ssmlHeadersPlusData)

                continuation.invokeOnCancellation {
                    client.cancel()
                }

                // Thêm timeout để tránh chờ vô hạn
                GlobalScope.launch {
                    delay(10000) // 10 giây timeout
                    if (continuation.isActive) {
                        // File vẫn có thể đã được tạo nhưng callback không được gọi
                        val checkFile = File(storage, fileName)
                        if (checkFile.exists() && checkFile.length() > 0) {
                            continuation.resume(checkFile.absolutePath)
                        } else {
                            continuation.resumeWithException(RuntimeException("Timeout waiting for file"))
                        }
                    }
                }
            }
        }
    }


    private fun dateToString(date: Date): String {
        val sdf = SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z (zzzz)",Locale.getDefault())
        return sdf.format(date)
    }

    private fun uuid(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }
    private fun removeIncompatibleCharacters(input: String): String? {
        if (input.isBlank()){
            return null
        }
        val output = StringBuilder()
        for (element in input) {
            val code = element.code
            if (code in 0..8 || code in 11..12 || code in 14..31) {
                output.append(" ")
            } else {
                output.append(element)
            }
        }
        return output.toString()
    }
}