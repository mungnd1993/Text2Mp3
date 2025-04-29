package com.texttomp3.texttospeech.ui.fragments

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.texttomp3.texttospeech.adapters.VoiceAdapter
import com.texttomp3.texttospeech.data.models.LanguageItem
import com.texttomp3.texttospeech.data.models.Voice
import com.texttomp3.texttospeech.base.BaseBottomSheetFragment
import com.texttomp3.texttospeech.databinding.FragmentVoiceBottomSheetBinding
import com.texttomp3.texttospeech.helpers.LanguageHelper
import com.texttomp3.texttospeech.utils.Constants.ANDROID
import com.texttomp3.texttospeech.utils.Constants.HELLO
import com.texttomp3.texttospeech.utils.Coroutines
import com.texttomp3.texttospeech.utils.Utils
import icu.xmc.edgettslib.TTS
import icu.xmc.edgettslib.TTSVoice
import java.io.File

class VoiceBottomSheet(
    private val listener: OnSelectVoiceListener,
    private val mode: String,
    private var voices: List<Voice>
) : BaseBottomSheetFragment<FragmentVoiceBottomSheetBinding>(), VoiceAdapter.OnClickListener,
    TextToSpeech.OnInitListener {

    private lateinit var adapter: VoiceAdapter
    private lateinit var textToSpeech: TextToSpeech
    private val tts = TTS.getInstance()
    private var mediaPlayer = MediaPlayer()
    private val languageList: List<LanguageItem> by lazy {
        LanguageHelper(requireContext()).getLanguageList()
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        boolean: Boolean
    ): FragmentVoiceBottomSheetBinding {
        return FragmentVoiceBottomSheetBinding.inflate(inflater, container, boolean)
    }

    override fun initMain() {
        initView()
        initEvent()
    }

    private fun initView() {
        textToSpeech = TextToSpeech(requireContext(), this)
        with(binding) {
            adapter = VoiceAdapter(mode,this@VoiceBottomSheet)
            rvVoice.adapter = adapter
            rvVoice.layoutManager = LinearLayoutManager(requireContext())
            voices = voices.sortedByDescending { it.isSelected }
            adapter.submitData(voices)
        }
    }

    private fun initEvent() {
        with(binding) {
            ivBack.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        deleteTTSCacheDir()
    }

    companion object {
        @JvmStatic
        fun newInstance(listener: OnSelectVoiceListener, mode: String, voices: List<Voice>) =
            VoiceBottomSheet(listener, mode, voices)
    }

    override fun onClick(voice: Voice) {
        for (v in voices) {
            v.isSelected = v.name == voice.name
        }
        adapter.submitData(voices)
        listener.onSelectVoice(voice)
        dismiss()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onDemo(voice: Voice) {
        if (voice.isPlaying) {
            if (mode == ANDROID) {
                textToSpeech.stop()
            } else {
                mediaPlayer.stop()
            }
            voices.forEach { it.isPlaying = false }
            adapter.notifyDataSetChanged()
        } else {
            val demoText = languageList.find { it.voice == voice.name }?.demo ?: HELLO

            voices.forEach { it.isPlaying = false }
            voices.find { it.name == voice.name }?.isPlaying = true
            adapter.notifyDataSetChanged()

            if (mode == ANDROID) {
                for (item in textToSpeech.voices) {
                    if (item.name == voice.name) {
                        textToSpeech.voice = item
                        break
                    }
                }
                textToSpeech.speak(demoText, TextToSpeech.QUEUE_FLUSH, null, null)
                Coroutines.main {
                    kotlinx.coroutines.delay(2000)
                    voices.forEach { it.isPlaying = false }
                    adapter.notifyDataSetChanged()
                }
            } else {
                val selectVoice =
                    TTSVoice(requireContext()).getVoiceList().find { it.ShortName == voice.name }
                if (selectVoice != null) {
                    tts.initialize(requireContext(), selectVoice)
                }
                Coroutines.io {
                    val path = tts.findHeadHook().speak(demoText)
                    mediaPlayer.reset()
                    mediaPlayer = MediaPlayer()
                    mediaPlayer.setDataSource(path)
                    mediaPlayer.prepareAsync()
                    mediaPlayer.setOnPreparedListener {
                        mediaPlayer.start()
                    }
                    mediaPlayer.setOnCompletionListener {
                        voices.forEach { it.isPlaying = false }
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    interface OnSelectVoiceListener {
        fun onSelectVoice(voice: Voice)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            Utils.log("OK")
        }
    }

    private fun deleteTTSCacheDir() {
        val ttsCacheDir = File(requireContext().cacheDir, "TTS")
        if (ttsCacheDir.exists()) {
            ttsCacheDir.deleteRecursively()
        }
    }

}