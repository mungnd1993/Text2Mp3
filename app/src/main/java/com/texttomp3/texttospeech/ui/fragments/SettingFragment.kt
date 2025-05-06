package com.texttomp3.texttospeech.ui.fragments

import android.content.Intent
import android.net.Uri
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.texttomp3.texttospeech.R
import com.texttomp3.texttospeech.base.BaseFragment
import com.texttomp3.texttospeech.databinding.FragmentSettingBinding
import com.texttomp3.texttospeech.ui.activities.FeedbackActivity
import com.texttomp3.texttospeech.utils.Constants.LINK_GOOGLE_PLAY
import com.texttomp3.texttospeech.utils.Constants.LINK_PRIVACY
import com.texttomp3.texttospeech.utils.Constants.LINK_SEARCH_GOOGLE_PLAY
import com.texttomp3.texttospeech.utils.Constants.QUERY_APP
import com.texttomp3.texttospeech.utils.Constants.RATE_BOTTOM_SHEET
import com.texttomp3.texttospeech.utils.Constants.SET_PACKAGE
import com.texttomp3.texttospeech.utils.Utils
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.texttomp3.texttospeech.ui.activities.PremiumActivity
import com.texttomp3.texttospeech.viewmodels.SettingViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel

class SettingFragment : BaseFragment<FragmentSettingBinding>() {
    private val settingViewModel: SettingViewModel by lazy {
        getViewModel<SettingViewModel>()
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        boolean: Boolean
    ): FragmentSettingBinding {
        return FragmentSettingBinding.inflate(inflater, container, boolean)
    }

    override fun initMain() {
        initView()
        initEvent()
    }

    private fun initView() {
        with(binding) {
            Utils.setGradientText(tvGetPro)

            val marginTop20dp = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                20f,
                clPrivacy.resources.displayMetrics
            ).toInt()

            viewLifecycleOwner.lifecycleScope.launch {
                settingViewModel.proVersion.collect {
                    if (it) {
                        cvPr.visibility = View.GONE
                        clPrivacy.apply {
                            val layoutParams = layoutParams as? ViewGroup.MarginLayoutParams
                            layoutParams?.topMargin = 0
                            this.layoutParams = layoutParams
                        }
                    } else {
                        cvPr.visibility = View.VISIBLE
                        clPrivacy.apply {
                            val layoutParams = layoutParams as? ViewGroup.MarginLayoutParams
                            layoutParams?.topMargin = marginTop20dp
                            this.layoutParams = layoutParams
                        }
                    }
                }
            }

            tvVersion.text = Utils.getAppVersion(requireContext())
        }
    }

    private fun initEvent() {
        with(binding) {
            btTry.setOnClickListener {
               if (settingViewModel.proVersion.value) {
                   Utils.toast(requireContext(), getString(R.string.already_subscribed))
               } else {
                   requireActivity().supportFragmentManager.beginTransaction()
                       .add(R.id.fcv_main2, Upgrade2Fragment.newInstance())
                       .addToBackStack(null)
                       .commit()
               }
            }

            clPrivacy.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, LINK_PRIVACY.toUri()))
            }

            clPremium.setOnClickListener {
                if (settingViewModel.proVersion.value) {
                    val intent = Intent(requireContext(), PremiumActivity::class.java)
                    startActivity(intent)
                } else {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .add(R.id.fcv_main2, Upgrade2Fragment.newInstance())
                        .addToBackStack(null)
                        .commit()
                }
            }

            clFeedback.setOnClickListener {
                val intent = Intent(requireContext(), FeedbackActivity::class.java)
                startActivity(intent)
            }

            clRateApp.setOnClickListener {
                val rateBottomSheet = RateBottomSheet.newInstance()
                rateBottomSheet.show(parentFragmentManager, RATE_BOTTOM_SHEET)
            }

            clShareApp.setOnClickListener {
                shareApp()
            }

            clMoreApp.setOnClickListener {
                openListAppCHPlay()
            }
        }
    }

    private fun shareApp() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.text_share_app_title))
            var shareMessage = getString(R.string.text_recommend)
            shareMessage += "\n" + LINK_GOOGLE_PLAY + requireContext().packageName + "\n\n"
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(
                Intent.createChooser(
                    shareIntent, getString(R.string.text_choose_tools_share_app)
                )
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun openListAppCHPlay() {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = (LINK_SEARCH_GOOGLE_PLAY + QUERY_APP).toUri()
            intent.setPackage(SET_PACKAGE)
            startActivity(intent)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = SettingFragment()
    }
}