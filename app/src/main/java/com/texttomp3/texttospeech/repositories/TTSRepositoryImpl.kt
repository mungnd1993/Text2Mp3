package com.texttomp3.texttospeech.repositories

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import com.texttomp3.texttospeech.R
import com.texttomp3.texttospeech.data.models.Language
import com.texttomp3.texttospeech.data.models.Project
import com.texttomp3.texttospeech.data.rooms.ProjectDatabase
import com.texttomp3.texttospeech.helpers.PreferenceHelper
import com.texttomp3.texttospeech.utils.Constants.ANDROID
import com.texttomp3.texttospeech.utils.Constants.DISPLAY_INDEX
import com.texttomp3.texttospeech.utils.Constants.GENDER
import com.texttomp3.texttospeech.utils.Constants.LANGUAGE
import com.texttomp3.texttospeech.utils.Constants.MODE
import com.texttomp3.texttospeech.utils.Constants.PITCH
import com.texttomp3.texttospeech.utils.Constants.SPEED
import com.texttomp3.texttospeech.utils.Constants.VOICE
import com.texttomp3.texttospeech.utils.Constants.VOLUME
import com.texttomp3.texttospeech.utils.Utils
import icu.xmc.edgettslib.TTS
import icu.xmc.edgettslib.TTSVoice
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class TTSRepositoryImpl(private val context: Context) : TTSRepository, TextToSpeech.OnInitListener {
    private val db = ProjectDatabase.getInstance(context)
    private val projectDAO = db.projectDao()

    private var textToSpeech = TextToSpeech(context, this)
    private val tts = TTS.getInstance()

    private var voicesAndroid = mutableListOf<Voice>()
    private val voicesEdge = TTSVoice(context).getVoiceList()

    private var isInitTTS = false
    private var initCompleteFuture = CompletableDeferred<Boolean>()

    override suspend fun generate(
        mode: String,
        language: String,
        voice: String,
        gender: String,
        displayIndex: Int,
        pitch: Float,
        speed: Float,
        volume: Float,
        text: String
    ): Project {
        // Đảm bảo TTS đã khởi tạo khi mode là ANDROID
        if (mode == ANDROID && !isInitTTS) {
            waitForInit()
        }

        val newestProject =
            projectDAO.getNewestProject() ?: Project(0, "", "", "", "", "",displayIndex,"", "", 0f, 0f, 0f, 0)
        if (mode == ANDROID) {
            for (item in voicesAndroid) {
                if (item.name == voice) {
                    textToSpeech.voice = item
                    break
                }
            }
            textToSpeech.setPitch(pitch)
            textToSpeech.setSpeechRate(speed)
            val path = synthesizeToMp3(text)
            val project = Project(
                name = "${context.getString(R.string.project)} ${newestProject.id + 1}",
                mode = mode,
                language = language,
                voice = voice,
                gender = gender,
                displayIndex = displayIndex,
                content = text,
                filePath = path,
                pitch = pitch,
                speed = speed,
                volume = volume,
                time = System.currentTimeMillis()
            )
            val id = projectDAO.insertProject(project)
            val createdProject = projectDAO.getProject(id)
            return createdProject
        } else {
            for (item in voicesEdge) {
                if (item.ShortName == voice) {
                    tts.initialize(context, item)
                    break
                }
            }
            tts.setVoicePitch(((pitch - 1.0f) * 50).toInt())
            tts.setVoiceRate(((speed - 1.0f) * 50).toInt())
            tts.setVoiceVolume((volume - 0.5f).toInt() * 100)
            val path = tts.findHeadHook().save(text)
            val project = Project(
                name = "${context.getString(R.string.project)} ${newestProject.id + 1}",
                mode = mode,
                language = language,
                voice = voice,
                gender = gender,
                displayIndex = displayIndex,
                content = text,
                filePath = path,
                pitch = pitch,
                speed = speed,
                volume = volume,
                time = System.currentTimeMillis()
            )
            val id = projectDAO.insertProject(project)
            val createdProject = projectDAO.getProject(id)
            return createdProject
        }
    }

    override suspend fun reGenerate(
        id: Long,
        mode: String,
        language: String,
        voice: String,
        gender: String,
        displayIndex: Int,
        pitch: Float,
        speed: Float,
        volume: Float,
        text: String
    ): String {
        // Đảm bảo TTS đã khởi tạo khi mode là ANDROID
        if (mode == ANDROID && !isInitTTS) {
            waitForInit()
        }

        val currentProject = projectDAO.getProject(id)
        if (mode == ANDROID) {
            for (item in voicesAndroid) {
                if (item.name == voice) {
                    textToSpeech.voice = item
                    break
                }
            }
            textToSpeech.setPitch(pitch)
            textToSpeech.setSpeechRate(speed)
            val path = synthesizeToMp3(text)
            val project = Project(
                id = id,
                name = currentProject.name,
                mode = mode,
                language = language,
                voice = voice,
                gender = gender,
                displayIndex = displayIndex,
                content = text,
                filePath = path,
                pitch = pitch,
                speed = speed,
                volume = volume,
                time = System.currentTimeMillis()
            )
            projectDAO.updateProject(project)
            return path
        } else {
            for (item in voicesEdge) {
                if (item.ShortName == voice) {
                    tts.initialize(context, item)
                    break
                }
            }
            tts.setVoicePitch((pitch - 1).toInt() * 100)
            tts.setVoiceRate((speed - 1).toInt() * 100)
            tts.setVoiceVolume((volume - 0.5f).toInt() * 100)
            val path = tts.findHeadHook().save(text)
            val project = Project(
                id = id,
                name = currentProject.name,
                mode = mode,
                language = language,
                voice = voice,
                gender = gender,
                displayIndex = displayIndex,
                content = text,
                filePath = path,
                pitch = pitch,
                speed = speed,
                volume = volume,
                time = System.currentTimeMillis()
            )
            projectDAO.updateProject(project)
            return path
        }
    }

    // Hàm mới để đợi TTS initialization hoàn tất
    private suspend fun waitForInit(): Boolean {
        return initCompleteFuture.await()
    }

    override suspend fun getLanguages(mode: String): List<Language> {
        // Đảm bảo TTS đã khởi tạo khi mode là ANDROID
        if (mode == ANDROID && !isInitTTS) {
            waitForInit()
        }

        return if (mode == ANDROID) {
            val uniqueCountries = voicesAndroid
                .map { it.locale.country }
                .filter { it.length == 2 }
                .distinct()
                .map { Language(it, false) }
            uniqueCountries
        } else {
            val uniqueCountries = voicesEdge
                .map { it.Locale.split("-").last() }
                .filter { it.length == 2 }
                .distinct()
                .map { Language(it, false) }

            uniqueCountries
        }
    }

    override suspend fun getVoices(
        mode: String,
        language: String
    ): List<com.texttomp3.texttospeech.data.models.Voice> {
        // Đảm bảo TTS đã khởi tạo khi mode là ANDROID
        if (mode == ANDROID && !isInitTTS) {
            waitForInit()
        }

        val voices = mutableListOf<com.texttomp3.texttospeech.data.models.Voice>()
        if (mode == ANDROID) {
            for (voice in voicesAndroid) {
                if (voice.locale.country == language) {
                    voices.add(com.texttomp3.texttospeech.data.models.Voice(voice.name, "", isSelected = false, isPlaying = false, 0))
                }
            }
            voices.forEachIndexed { index, voice ->
                voice.displayIndex = index + 1
            }
        } else {
            for (voice in voicesEdge) {
                if (voice.Locale.split("-").last() == language) {
                    voices.add(
                        com.texttomp3.texttospeech.data.models.Voice(
                            voice.ShortName,
                            voice.Gender, isSelected = false,
                            isPlaying = false
                        )
                    )
                }
            }
        }
        return voices
    }

    /**
     * Lưu setting cho lần sử dụng sau
     */
    override fun setSetting(
        mode: String,
        language: String,
        voice: com.texttomp3.texttospeech.data.models.Voice,
        pitch: Float,
        speed: Float,
        volume: Float
    ) {
        PreferenceHelper.getInstance(context).putString(MODE, mode)
        PreferenceHelper.getInstance(context).putString(LANGUAGE, language)
        PreferenceHelper.getInstance(context).putString(VOICE, voice.name)
        PreferenceHelper.getInstance(context).putString(GENDER, voice.gender)
        PreferenceHelper.getInstance(context).putInt(DISPLAY_INDEX, voice.displayIndex)
        PreferenceHelper.getInstance(context).putFloat(PITCH, pitch)
        PreferenceHelper.getInstance(context).putFloat(SPEED, speed)
        PreferenceHelper.getInstance(context).putFloat(VOLUME, volume)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitTTS = true
            val voices = textToSpeech.voices
            if (voices != null && voices.isNotEmpty()) {
                voicesAndroid.clear()
                voicesAndroid = voices.toMutableList()
            }
            initCompleteFuture.complete(true)
        } else {
            initCompleteFuture.complete(false)
        }
    }

    /**
     * Chuyển wav sang mp3
     */
    private suspend fun synthesizeToMp3(text: String): String {
        val wavFile = File(context.filesDir, "${System.currentTimeMillis()}.wav")

        return suspendCancellableCoroutine { continuation ->
            textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    Utils.log("Bắt đầu tạo file WAV")
                }

                override fun onDone(utteranceId: String?) {
                    if (utteranceId == "tts_output") {
                        if (wavFile.exists() && wavFile.length() > 0) {
                            continuation.resume(wavFile.absolutePath)
                        } else {
                            continuation.resumeWithException(Exception("File WAV không hợp lệ"))
                        }
                    }
                }

                override fun onError(utteranceId: String?) {
                    Utils.log("Lỗi: Quá trình tạo file WAV thất bại")
                    continuation.resumeWithException(Exception("Quá trình tạo file WAV thất bại"))
                }
            })

            textToSpeech.synthesizeToFile(text, null, wavFile, "tts_output")
        }
    }
}