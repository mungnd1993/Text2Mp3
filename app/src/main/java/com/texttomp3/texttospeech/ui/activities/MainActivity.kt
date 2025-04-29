package com.texttomp3.texttospeech.ui.activities

import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.texttomp3.texttospeech.data.models.Voice
import com.texttomp3.texttospeech.ui.fragments.HomeFragment
import com.texttomp3.texttospeech.ui.fragments.SettingFragment
import com.texttomp3.texttospeech.R
import com.texttomp3.texttospeech.ads.NativeAds
import com.texttomp3.texttospeech.ads.NativeAdsListener
import com.texttomp3.texttospeech.base.BaseActivity
import com.texttomp3.texttospeech.databinding.ActivityMainBinding
import com.texttomp3.texttospeech.helpers.PreferenceHelper
import com.texttomp3.texttospeech.utils.Constants.ANDROID
import com.texttomp3.texttospeech.utils.Constants.DISPLAY_INDEX
import com.texttomp3.texttospeech.utils.Constants.GB
import com.texttomp3.texttospeech.utils.Constants.GENDER
import com.texttomp3.texttospeech.viewmodels.TTSViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import com.texttomp3.texttospeech.utils.Constants.MODE
import com.texttomp3.texttospeech.utils.Constants.VOICE
import com.texttomp3.texttospeech.utils.Constants.LANGUAGE
import com.texttomp3.texttospeech.utils.Constants.PITCH
import com.texttomp3.texttospeech.utils.Constants.SPEED
import com.texttomp3.texttospeech.utils.Constants.VOICE_DEFAULT
import com.texttomp3.texttospeech.utils.Constants.VOLUME
import com.texttomp3.texttospeech.viewmodels.SettingViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : BaseActivity<ActivityMainBinding>() {
    private val ttsViewModel: TTSViewModel by lazy {
        getViewModel<TTSViewModel>()
    }

    private val settingViewModel: SettingViewModel by lazy {
        getViewModel<SettingViewModel>()
    }

    override fun createBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun initMain() {
        initView()
        initEvent()
    }

    private fun initView() {
        with(binding) {
            lifecycleScope.launch {
                settingViewModel.proVersion.collect {
                    if (!it) {
                        displayAds()
                    }
                }
            }

            /**
             * Lấy setting từ lần sử dụng trước
             */
            val savedMode =
                PreferenceHelper.getInstance(this@MainActivity).getString(MODE, ANDROID)
            val savedLanguage =
                PreferenceHelper.getInstance(this@MainActivity).getString(LANGUAGE, GB)
            val savedVoice =
                PreferenceHelper.getInstance(this@MainActivity)
                    .getString(VOICE, VOICE_DEFAULT)
            val savedGender = PreferenceHelper.getInstance(this@MainActivity).getString(GENDER, "")
            val savedIndex = PreferenceHelper.getInstance(this@MainActivity).getInt(DISPLAY_INDEX, 1)
            val savedPitch =
                PreferenceHelper.getInstance(this@MainActivity).getFloat(PITCH, 1.0f)
            val savedSpeed =
                PreferenceHelper.getInstance(this@MainActivity).getFloat(SPEED, 1.0f)
            val savedVolume =
                PreferenceHelper.getInstance(this@MainActivity).getFloat(VOLUME, 0.5f)

            ttsViewModel.init(
                savedMode,
                savedLanguage,
                Voice(savedVoice, savedGender, isSelected = true, isPlaying = false, savedIndex),
                savedPitch,
                savedSpeed,
                savedVolume
            )

            addFragment(fcvMain.id, HomeFragment.newInstance(), isReplace = true, true)
        }
    }

    private fun initEvent() {
        with(binding) {
            clHome.setOnClickListener {
                if (supportFragmentManager.findFragmentById(R.id.fcv_main) !is HomeFragment) {
                    addFragment(
                        binding.fcvMain.id,
                        HomeFragment.newInstance(),
                        isReplace = true,
                        true
                    )
                    ivHome.setImageResource(R.drawable.ic_home_active)
                    tvHome.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.black))
                    ivSetting.setImageResource(R.drawable.ic_setting)
                    tvSetting.setTextColor(
                        ContextCompat.getColor(this@MainActivity, R.color.grey_text_main)
                    )
                    clContainAd.visibility = View.VISIBLE
                }
            }

            ivAdd.setOnClickListener {
                val intent = Intent(this@MainActivity, TTSActivity::class.java)
                startActivity(intent)
            }

            clSetting.setOnClickListener {
                if (supportFragmentManager.findFragmentById(R.id.fcv_main) !is SettingFragment) {
                    addFragment(
                        binding.fcvMain.id,
                        SettingFragment.newInstance(),
                        isReplace = true,
                        true
                    )
                    ivHome.setImageResource(R.drawable.ic_home)
                    tvHome.setTextColor(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.grey_text_main
                        )
                    )
                    ivSetting.setImageResource(R.drawable.ic_setting_active)
                    tvSetting.setTextColor(
                        ContextCompat.getColor(this@MainActivity, R.color.black)
                    )
                    clContainAd.visibility = View.GONE
                }
            }
        }
    }

    private fun displayAds() {
        NativeAds.getInstance().navigateAds(
            this,
            binding.flAdPlaceholderLanguage,
            R.layout.layout_ads_native_custom_4,
            object : NativeAdsListener {
                override fun onFail() {
                    binding.clContainAd.visibility = View.GONE
                    binding.vDivider1.visibility = View.GONE
                }

                override fun onSuccess() {
                    binding.vDivider1.visibility = View.VISIBLE
                    binding.clContainAd.visibility = View.VISIBLE
                    binding.flLoading.visibility = View.GONE
                }

            })
    }
}