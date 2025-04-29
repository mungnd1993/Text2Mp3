package com.texttomp3.texttospeech.ui.fragments

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import com.texttomp3.texttospeech.R
import com.texttomp3.texttospeech.base.BaseFragment
import com.texttomp3.texttospeech.databinding.FragmentSettingBinding
import com.texttomp3.texttospeech.ui.activities.FeedbackActivity
import com.texttomp3.texttospeech.utils.Constants.LINK_GOOGLE_PLAY
import com.texttomp3.texttospeech.utils.Constants.LINK_PRIVACY
import com.texttomp3.texttospeech.utils.Constants.LINK_SEARCH_GOOGLE_PLAY
import com.texttomp3.texttospeech.utils.Constants.NORMAL
import com.texttomp3.texttospeech.utils.Constants.QUERY_APP
import com.texttomp3.texttospeech.utils.Constants.RATE_BOTTOM_SHEET
import com.texttomp3.texttospeech.utils.Constants.SET_PACKAGE
import com.texttomp3.texttospeech.utils.Utils

class SettingFragment : BaseFragment<FragmentSettingBinding>() {
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

            tvVersion.text = Utils.getAppVersion(requireContext())
        }
    }

    private fun initEvent() {
        with(binding) {
            btTry.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .add(R.id.fcv_main2, Upgrade2Fragment.newInstance(NORMAL))
                    .addToBackStack(null)
                    .commit()
            }

            clPrivacy.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(LINK_PRIVACY)))
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
            intent.data = Uri.parse(
                LINK_SEARCH_GOOGLE_PLAY + QUERY_APP
            )
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