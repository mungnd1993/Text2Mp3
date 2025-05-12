package com.texttomp3.texttospeech.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.view.View
import android.widget.SeekBar
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.texttomp3.texttospeech.data.models.TTSSettings
import com.texttomp3.texttospeech.data.models.Voice
import com.texttomp3.texttospeech.ui.fragments.MoreBottomSheet
import com.texttomp3.texttospeech.R
import com.texttomp3.texttospeech.ads.LoadInterstitialAds
import com.texttomp3.texttospeech.base.BaseActivity
import com.texttomp3.texttospeech.databinding.ActivityTtsBinding
import com.texttomp3.texttospeech.helpers.LanguageHelper
import com.texttomp3.texttospeech.ui.fragments.Upgrade2Fragment
import com.texttomp3.texttospeech.utils.Constants.ANDROID
import com.texttomp3.texttospeech.utils.Constants.CONTENT
import com.texttomp3.texttospeech.utils.Constants.DISPLAY_INDEX
import com.texttomp3.texttospeech.utils.Constants.FILE_PATH
import com.texttomp3.texttospeech.utils.Constants.GB
import com.texttomp3.texttospeech.utils.Constants.GENDER
import com.texttomp3.texttospeech.utils.Constants.ID
import com.texttomp3.texttospeech.utils.Constants.LANGUAGE
import com.texttomp3.texttospeech.utils.Constants.MODE
import com.texttomp3.texttospeech.utils.Constants.MORE_BOTTOM_SHEET
import com.texttomp3.texttospeech.utils.Constants.PITCH
import com.texttomp3.texttospeech.utils.Constants.SPEED
import com.texttomp3.texttospeech.utils.Constants.VOICE
import com.texttomp3.texttospeech.utils.Constants.VOICE_DEFAULT
import com.texttomp3.texttospeech.utils.Constants.VOLUME
import com.texttomp3.texttospeech.utils.Utils
import com.texttomp3.texttospeech.utils.Utils.getSavedSettings
import com.texttomp3.texttospeech.viewmodels.HomeViewModel
import com.texttomp3.texttospeech.viewmodels.SettingViewModel
import com.texttomp3.texttospeech.viewmodels.TTSViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.io.File

class TTSActivity : BaseActivity<ActivityTtsBinding>() {
    private var mediaPlayer: MediaPlayer? = null
    private var selectedSetting =
        TTSSettings(
            ANDROID,
            GB,
            Voice(VOICE_DEFAULT, "", isSelected = true, isPlaying = false),
            1.0f,
            1.0f,
            0.5f
        )

    private val homeViewModel: HomeViewModel by lazy {
        getViewModel<HomeViewModel>()
    }

    private val ttsViewModel: TTSViewModel by lazy {
        getViewModel<TTSViewModel>()
    }

    private val settingViewModel: SettingViewModel by lazy {
        getViewModel<SettingViewModel>()
    }

    private val ttsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                data?.let {
                    val mode = it.getStringExtra(MODE)
                    val language = it.getStringExtra(LANGUAGE)
                    val voice = it.getStringExtra(VOICE)
                    val gender = it.getStringExtra(GENDER)
                    val displayIndex = it.getIntExtra(DISPLAY_INDEX, 1)
                    val pitch = it.getFloatExtra(PITCH, 1.0f)
                    val speed = it.getFloatExtra(SPEED, 1.0f)
                    val volume = it.getFloatExtra(VOLUME, 0.5f)
                    ttsViewModel.init(
                        mode!!,
                        language!!,
                        Voice(
                            voice!!,
                            gender!!,
                            isSelected = true,
                            isPlaying = false,
                            displayIndex
                        ),
                        pitch,
                        speed,
                        volume
                    )
                }
            }
        }

    override fun createBinding(): ActivityTtsBinding {
        return ActivityTtsBinding.inflate(layoutInflater)
    }

    override fun initMain() {
        initView()
        initEvent()

        handleOnBackPressed()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        val languageList = LanguageHelper(this).getLanguageList()

        /**
         * Lấy setting trước đó
         */
        val savedSettings = getSavedSettings(this)
        ttsViewModel.init(
            savedSettings.mode,
            savedSettings.language,
            savedSettings.voice,
            savedSettings.pitch,
            savedSettings.speed,
            savedSettings.volume
        )

        /**
         * Lấy setting từ project được chọn
         */
        val mode = intent.getStringExtra(MODE)
        val language = intent.getStringExtra(LANGUAGE)
        val voice = intent.getStringExtra(VOICE)
        val gender = intent.getStringExtra(GENDER)
        val displayIndex = intent.getIntExtra(DISPLAY_INDEX, 1)
        val pitch = intent.getFloatExtra(PITCH, 0.0f)
        val speed = intent.getFloatExtra(SPEED, 0.0f)
        val volume = intent.getFloatExtra(VOLUME, 0.0f)
        val id = intent.getLongExtra(ID, 0)
        val content = intent.getStringExtra(CONTENT)
        val filePath = intent.getStringExtra(FILE_PATH)
        if (mode != null && language != null && voice != null && gender != null && id != 0L && content != null && filePath != null && pitch != 0.0f && speed != 0.0f && volume != 0.0f) {
            ttsViewModel.init(
                mode,
                language,
                Voice(voice, gender, isSelected = true, isPlaying = false, displayIndex),
                pitch,
                speed,
                volume
            )
            ttsViewModel.setTextAndPath(id, content, filePath)
        }

        with(binding) {
            lifecycleScope.launch {
                ttsViewModel.id.collect {
                    if (it != 0L) {
                        ivMore.visibility = View.VISIBLE
                    } else {
                        ivMore.visibility = View.GONE
                    }
                }
            }

            lifecycleScope.launch {
                ttsViewModel.currentSetting.collect {
                    selectedSetting = it.copy()
                    tvLanguage.text = languageList.find { l -> l.code == it.language }?.name
                    if (it.mode == ANDROID) {
                        tvVoice.text = "${getString(R.string.voice)} ${it.voice.displayIndex}"
                    } else {
                        tvVoice.text =
                            languageList.find { l -> l.voice == it.voice.name }?.voiceName
                    }
                    ivImage.setCountryCode(it.language)
                }
            }

            lifecycleScope.launch {
                ttsViewModel.text.collect {
                    if (it.isNotEmpty()) {
                        btGenerate.setBackgroundColor(getColor(R.color.blue_bold))
                        btGenerate.setTextColor(getColor(R.color.white))
                    }
                    btGenerate.isEnabled = it.isNotEmpty()
                    etText.setText(it)
                    tvCurrentCharacter.text = it.length.toString()
                }
            }

            lifecycleScope.launch {
                ttsViewModel.path.collect {
                    if (it.isNotEmpty()) {
                        if (!settingViewModel.proVersion.value) {
                            supportFragmentManager.popBackStack()
                            LoadInterstitialAds.getInstance().showInterstitial(this@TTSActivity)
                        } else {
                            delay(500)
                            supportFragmentManager.popBackStack()
                        }
                        clAudio.visibility = View.VISIBLE
                        btGenerate.text = getString(R.string.re_generate)

                        mediaPlayer?.reset()
                        mediaPlayer = MediaPlayer()
                        mediaPlayer?.apply {
                            setDataSource(it)
                            prepareAsync()
                            setOnPreparedListener { mp ->
                                binding.sbAudio.progress = 0
                                binding.tvMin.text = Utils.convertDuration(0)
                                binding.sbAudio.max = mp.duration
                                binding.tvMax.text = Utils.convertDuration(mp.duration)
                                updateProgress()
                            }
                            setOnCompletionListener {
                                seekTo(0)
                                binding.tvMin.text = Utils.convertDuration(0)
                                binding.sbAudio.progress = 0
                                binding.ivPlay.setImageResource(R.drawable.ic_play)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initEvent() {
        with(binding) {
            ivMore.setOnClickListener {
                try {
                    mediaPlayer?.let { mp ->
                        if (mp.isPlaying) {
                            mp.pause()
                            ivPlay.setImageResource(R.drawable.ic_play)
                        }
                    }
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }

                lifecycleScope.launch {
                    val project = homeViewModel.getProject(ttsViewModel.id.value)
                    val moreBottomSheet = MoreBottomSheet.newInstance(project)
                    moreBottomSheet.show(supportFragmentManager, MORE_BOTTOM_SHEET)
                }
            }

            clLanguage.setOnClickListener {
                try {
                    mediaPlayer?.let { mp ->
                        if (mp.isPlaying) {
                            mp.pause()
                            ivPlay.setImageResource(R.drawable.ic_play)
                        }
                    }
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }
                val intent = Intent(this@TTSActivity, SelectVoiceActivity::class.java)
                intent.putExtra(MODE, selectedSetting.mode)
                intent.putExtra(LANGUAGE, selectedSetting.language)
                intent.putExtra(VOICE, selectedSetting.voice.name)
                intent.putExtra(GENDER, selectedSetting.voice.gender)
                intent.putExtra(DISPLAY_INDEX, selectedSetting.voice.displayIndex)
                intent.putExtra(PITCH, selectedSetting.pitch)
                intent.putExtra(SPEED, selectedSetting.speed)
                intent.putExtra(VOLUME, selectedSetting.volume)
                ttsLauncher.launch(intent)
            }

            etText.addTextChangedListener {
                tvCurrentCharacter.text = etText.text?.length.toString()
                if (etText.text.toString().isNotEmpty()) {
                    btGenerate.setBackgroundColor(getColor(R.color.blue_bold))
                    btGenerate.setTextColor(getColor(R.color.white))
                    btGenerate.isEnabled = true
                } else {
                    btGenerate.setBackgroundColor(getColor(R.color.grey_button_bold))
                    btGenerate.setTextColor(getColor(R.color.black))
                    btGenerate.isEnabled = false
                }
            }

            clClear.setOnClickListener {
                etText.text?.clear()

            }

            binding.ivPlay.setOnClickListener {
                mediaPlayer?.let {
                    if (it.isPlaying) {
                        it.pause()
                        binding.ivPlay.setImageResource(R.drawable.ic_play)
                    } else {
                        it.start()
                        binding.ivPlay.setImageResource(R.drawable.ic_pause)
                        updateProgress()
                    }
                }
            }


            sbAudio.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        mediaPlayer?.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            })

            ivDownload.setOnClickListener {
                if (settingViewModel.proVersion.value) {
                    val loadingFragment = Utils.showLoadingFragment(
                        supportFragmentManager,
                        R.id.fcv_main2,
                        getString(R.string.downloading_audio)
                    )

                    lifecycleScope.launch {
                        val success = withContext(Dispatchers.IO) {
                            Utils.saveMp3ToMediaStore(this@TTSActivity, File(ttsViewModel.path.value))
                        }

                        if (success) {
                            loadingFragment.showSuccessAndDismiss()
                        }
                    }
                } else {
                    val upgrade2Fragment = Upgrade2Fragment.newInstance()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fcv_main2, upgrade2Fragment)
                        .addToBackStack(null)
                        .commit()
                }
            }

            btGenerate.setOnClickListener {
                val text = etText.text.toString()
                ttsViewModel.generate(text)
                supportFragmentManager.let { fragmentManager ->
                    Utils.showLoadingFragment(
                        fragmentManager,
                        R.id.fcv_main2,
                        getString(R.string.loading_audio)
                    )
                }
            }
        }
    }

    private fun updateProgress() {
        lifecycleScope.launch {
            while (mediaPlayer?.isPlaying == true) {
                binding.ivPlay.setImageResource(R.drawable.ic_pause)
                binding.sbAudio.progress = mediaPlayer?.currentPosition ?: 0
                binding.tvMin.text = Utils.convertDuration(mediaPlayer?.currentPosition ?: 0)
                delay(100)
            }
        }
    }

    private fun handleOnBackPressed() {
        binding.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                try {
                    mediaPlayer?.let { mp ->
                        if (mp.isPlaying) mp.stop()
                        mp.reset()
                        mp.release()
                    }
                    mediaPlayer = null
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }

                ttsViewModel.resetPath()
                finish()
            }
        })
    }

}