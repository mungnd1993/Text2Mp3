package com.texttomp3.texttospeech.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.lifecycle.lifecycleScope
import com.texttomp3.texttospeech.R
import com.texttomp3.texttospeech.adapters.IntroAdapter
import com.texttomp3.texttospeech.ads.NativeAds
import com.texttomp3.texttospeech.ads.NativeAdsListener
import com.texttomp3.texttospeech.base.BaseFragment
import com.texttomp3.texttospeech.databinding.FragmentIntroBinding
import com.texttomp3.texttospeech.ui.activities.MainActivity
import com.texttomp3.texttospeech.viewmodels.SettingViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel

class IntroFragment : BaseFragment<FragmentIntroBinding>() {
    private var isLayout = 1
    private val settingViewModel: SettingViewModel by lazy {
        getViewModel()
    }
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        boolean: Boolean
    ): FragmentIntroBinding {
        return FragmentIntroBinding.inflate(inflater, container, boolean)
    }

    override fun initMain() {
        initView()
        initEvent()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initView() {
        with(binding) {
            viewLifecycleOwner.lifecycleScope.launch {
                settingViewModel.proVersion.collect {
                    if (!it) {
                        displayAds()
                    }
                }
            }

            val adapter = IntroAdapter(settingViewModel)
            vpIntro.adapter = adapter
            vpIntro.isUserInputEnabled = false

            diIndicator.dotsClickable = false
            diIndicator.attachTo(vpIntro)
        }
    }

    private fun initEvent() {
        with(binding) {
            btContinue.setOnClickListener {
                if (isLayout == 1) {

                    if (!settingViewModel.proVersion.value) {
                        val metrics = Resources.getSystem().displayMetrics
                        val heightDp = metrics.heightPixels / metrics.density

                        val dpValue1 = if (heightDp < 850) 20f else 27f

                        val layoutParams1 = binding.vpIntro.layoutParams as ViewGroup.MarginLayoutParams
                        layoutParams1.bottomMargin = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            dpValue1,
                            resources.displayMetrics
                        ).toInt()
                        binding.vpIntro.layoutParams = layoutParams1

                        val dpValue = if (heightDp < 850) 16f else 22f
                        val layoutParams = binding.diIndicator.layoutParams as ViewGroup.MarginLayoutParams
                        layoutParams.bottomMargin = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            dpValue,
                            resources.displayMetrics
                        ).toInt()
                        binding.diIndicator.layoutParams = layoutParams
                    }

                    isLayout = 2
                    vpIntro.currentItem += 1
                    btContinue.text = getString(R.string.continue_button)
                    btContinue.setTextColor(getColor(requireContext(), R.color.blue_bold))
                    btContinue.backgroundTintList =
                        ColorStateList.valueOf(getColor(requireContext(), R.color.blue_light))
                    clContainAd.visibility = View.VISIBLE
                } else {
                    if (settingViewModel.proVersion.value) {
                        // Nếu đã pro -> Mở MainActivity
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    } else {
                        // Nếu chưa pro -> Vẫn mở UpgradeFragment như cũ
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.fcv_main, UpgradeFragment.newInstance())
                            .commit()
                    }
                }
            }
        }
    }

    private fun displayAds() {
        NativeAds.getInstance().navigateAds(
            requireActivity(),
            binding.flAdPlaceholderLanguage,
            R.layout.layout_ads_native_custom_3,
            object : NativeAdsListener {
                override fun onFail() {
                    binding.clContainAd.visibility = View.GONE
                    binding.vDivider1.visibility = View.GONE
                }

                override fun onSuccess() {
                    binding.flLoading.visibility = View.GONE
                }

            })
    }

    companion object {
        @JvmStatic
        fun newInstance() = IntroFragment()
    }

}