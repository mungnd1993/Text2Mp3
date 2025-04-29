package com.texttomp3.texttospeech.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.widget.SeekBar
import androidx.lifecycle.lifecycleScope
import com.texttomp3.texttospeech.data.models.Language
import com.texttomp3.texttospeech.data.models.Mode
import com.texttomp3.texttospeech.data.models.TTSSettings
import com.texttomp3.texttospeech.data.models.Voice
import com.texttomp3.texttospeech.ui.fragments.LanguageBottomSheet
import com.texttomp3.texttospeech.ui.fragments.ModeBottomSheet
import com.texttomp3.texttospeech.ui.fragments.VoiceBottomSheet
import com.texttomp3.texttospeech.R
import com.texttomp3.texttospeech.ads.NativeAds
import com.texttomp3.texttospeech.ads.NativeAdsListener
import com.texttomp3.texttospeech.base.BaseActivity
import com.texttomp3.texttospeech.databinding.ActivitySelectVoiceBinding
import com.texttomp3.texttospeech.helpers.LanguageHelper
import com.texttomp3.texttospeech.utils.Constants.ANDROID
import com.texttomp3.texttospeech.utils.Constants.DISPLAY_INDEX
import com.texttomp3.texttospeech.utils.Constants.FEMALE
import com.texttomp3.texttospeech.utils.Constants.GB
import com.texttomp3.texttospeech.utils.Constants.GENDER
import com.texttomp3.texttospeech.utils.Constants.LANGUAGE
import com.texttomp3.texttospeech.utils.Constants.LANGUAGE_BOTTOM_SHEET
import com.texttomp3.texttospeech.utils.Constants.MALE
import com.texttomp3.texttospeech.utils.Constants.MODE
import com.texttomp3.texttospeech.utils.Constants.MODE_BOTTOM_SHEET
import com.texttomp3.texttospeech.utils.Constants.PITCH
import com.texttomp3.texttospeech.utils.Constants.SPEED
import com.texttomp3.texttospeech.utils.Constants.VOICE
import com.texttomp3.texttospeech.utils.Constants.VOICE_BOTTOM_SHEET
import com.texttomp3.texttospeech.utils.Constants.VOICE_DEFAULT
import com.texttomp3.texttospeech.utils.Constants.VOLUME
import com.texttomp3.texttospeech.utils.Utils.toCommaString
import com.texttomp3.texttospeech.viewmodels.SettingViewModel
import com.texttomp3.texttospeech.viewmodels.TTSViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel

class SelectVoiceActivity : BaseActivity<ActivitySelectVoiceBinding>(),
    ModeBottomSheet.OnSelectModeListener, LanguageBottomSheet.OnSelectLanguageListener,
    VoiceBottomSheet.OnSelectVoiceListener {

    private var mode = ""
    private var languages = mutableListOf<Language>()
    private var voices = mutableListOf<Voice>()

    private var selectedSetting = TTSSettings(
        ANDROID,
        GB,
        Voice(VOICE_DEFAULT, "", isSelected = true, isPlaying = false, 1),
        1.0f,
        1.0f,
        0.5f
    )

    private val ttsViewModel: TTSViewModel by lazy {
        getViewModel<TTSViewModel>()
    }

    private val settingViewModel: SettingViewModel by lazy {
        getViewModel<SettingViewModel>()
    }

    override fun createBinding(): ActivitySelectVoiceBinding {
        return ActivitySelectVoiceBinding.inflate(layoutInflater)
    }

    override fun initMain() {
        initView()
        initEvent()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        /**
         * Lấy setting được TTSActivity chuyển qua
         */
        val currentMode = intent.getStringExtra(MODE)
        val language = intent.getStringExtra(LANGUAGE)
        val voice = intent.getStringExtra(VOICE)
        val gender = intent.getStringExtra(GENDER)
        val displayIndex = intent.getIntExtra(DISPLAY_INDEX, 1)
        val pitch = intent.getFloatExtra(PITCH, 1.0f)
        val speed = intent.getFloatExtra(SPEED, 1.0f)
        val volume = intent.getFloatExtra(VOLUME, 0.5f)
        ttsViewModel.init(currentMode!!, language!!, Voice(voice!!, gender!!, isSelected = true, isPlaying = false, displayIndex), pitch, speed, volume)
        ttsViewModel.cloneSetting()

        val languageList = LanguageHelper(this).getLanguageList()

        with(binding) {
            lifecycleScope.launch {
                settingViewModel.proVersion.collect {
                    if (!it) {
                        displayAds()
                    }
                }
            }

            lifecycleScope.launch {
                ttsViewModel.showSetting.collect {
                    mode = it.mode
                    if (it.mode == ANDROID) {
                        tvMode.text = getString(R.string.default_voice_engine)
                        ivMode.setImageResource(R.drawable.ic_android)
                        tvVoice.text = "${getString(R.string.voice)} ${it.voice.displayIndex}"
                    } else {
                        tvMode.text = getString(R.string.edge_text_to_speech)
                        ivMode.setImageResource(R.drawable.ic_edge)
                        tvVoice.text = languageList.find { l -> l.voice == it.voice.name }?.voiceName
                    }

                    tvLanguage.text = languageList.find { l -> l.code == it.language }?.name
                    ivLanguage.setCountryCode(it.language)

                    when (it.voice.gender) {
                        MALE -> {
                            ivVoice.setImageResource(R.drawable.ic_male)
                        }

                        FEMALE -> {
                            ivVoice.setImageResource(R.drawable.ic_female)
                        }

                        else -> {
                            ivVoice.setImageResource(R.drawable.ic_speaker)
                        }
                    }

                    sbPitch.progress = (it.pitch * 100).toInt()
                    val percentPitch = sbPitch.progress - 100
                    tvPitchValue.text =
                        if (percentPitch > 0) "+$percentPitch%" else "$percentPitch%"

                    sbSpeed.progress = (it.speed * 100).toInt()
                    tvSpeedValue.text = "x${it.speed.toCommaString()}"

                    sbVolume.progress = (it.volume * 100).toInt()
                    val progressVolume = sbVolume.progress / 10 - 0
                    tvVolumeValue.text =
                        if (progressVolume > 0) "+${progressVolume}dB" else "${progressVolume}dB"
                }
            }

            lifecycleScope.launch {
                ttsViewModel.languages.collect {
                    languages = it.toMutableList()
                }
            }

            lifecycleScope.launch {
                ttsViewModel.voices.collect {
                    voices = it.toMutableList()
                }
            }

            lifecycleScope.launch {
                ttsViewModel.currentSetting.collect {
                    selectedSetting = it.copy()
                }
            }
        }
    }

    private fun initEvent() {
        with(binding) {
            ivBack.setOnClickListener {
                ttsViewModel.resetLanguagesAndVoices(0)
                finish()
            }

            clMode.setOnClickListener {
                val modeBottomSheet = ModeBottomSheet.newInstance(this@SelectVoiceActivity, mode)
                modeBottomSheet.show(supportFragmentManager, MODE_BOTTOM_SHEET)
            }

            clLanguage.setOnClickListener {
                val languageBottomSheet =
                    LanguageBottomSheet.newInstance(this@SelectVoiceActivity, languages.toList())
                languageBottomSheet.show(supportFragmentManager, LANGUAGE_BOTTOM_SHEET)
            }

            clVoice.setOnClickListener {
                val voiceBottomSheet =
                    VoiceBottomSheet.newInstance(this@SelectVoiceActivity, mode, voices.toList())
                voiceBottomSheet.show(supportFragmentManager, VOICE_BOTTOM_SHEET)
            }

            sbPitch.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    val pitch = progress / 100f
                    ttsViewModel.setShowSetting(null, null, null, pitch, null, null)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })

            sbSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    val speed = progress / 100f
                    ttsViewModel.setShowSetting(null, null, null, null, speed, null)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })

            sbVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    val volume = progress / 100f
                    ttsViewModel.setShowSetting(null, null, null, null, null, volume)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })

            btSave.setOnClickListener {
                ttsViewModel.setCurrentSetting()
                val intent = Intent()
                intent.putExtra(MODE, selectedSetting.mode)
                intent.putExtra(LANGUAGE, selectedSetting.language)
                intent.putExtra(VOICE, selectedSetting.voice.name)
                intent.putExtra(GENDER, selectedSetting.voice.gender)
                intent.putExtra(DISPLAY_INDEX, selectedSetting.voice.displayIndex)
                intent.putExtra(PITCH, selectedSetting.pitch)
                intent.putExtra(SPEED, selectedSetting.speed)
                intent.putExtra(VOLUME, selectedSetting.volume)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun displayAds() {
        NativeAds.getInstance().navigateAdsMedium(
            this,
            binding.flAdPlaceholderLanguage,
            R.layout.layout_ads_native_custom_1,
            object : NativeAdsListener {
                override fun onFail() {
                    binding.clContainAd.visibility = View.GONE
                }

                override fun onSuccess() {
                    binding.clContainAd.visibility = View.VISIBLE
                    binding.flLoading.visibility = View.GONE
                }

            })
    }

    override fun onSelectMode(mode: Mode) {
        this.mode = mode.name
        ttsViewModel.setShowSetting(mode.name, null, null, null, null, null)
    }

    override fun onSelectLanguage(language: Language) {
        ttsViewModel.setShowSetting(null, language.name, null, null, null, null)
    }

    override fun onSelectVoice(voice: Voice) {
        ttsViewModel.setShowSetting(null, null, Voice(voice.name, voice.gender, isSelected = true, isPlaying = false, voice.displayIndex), null, null, null)
    }

}