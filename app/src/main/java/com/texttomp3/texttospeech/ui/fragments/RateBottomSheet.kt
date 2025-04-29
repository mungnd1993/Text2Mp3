package com.texttomp3.texttospeech.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.MailTo
import com.texttomp3.texttospeech.R
import com.texttomp3.texttospeech.base.BaseBottomSheetFragment
import com.texttomp3.texttospeech.databinding.FragmentRateBottomSheetBinding
import com.texttomp3.texttospeech.utils.Constants.EMAIL
import com.texttomp3.texttospeech.utils.Constants.LINK_GOOGLE_PLAY
import com.texttomp3.texttospeech.utils.Constants.SET_PACKAGE
import com.texttomp3.texttospeech.utils.Utils

class RateBottomSheet : BaseBottomSheetFragment<FragmentRateBottomSheetBinding>() {
    private var rate = 5.0f

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        boolean: Boolean
    ): FragmentRateBottomSheetBinding {
        return FragmentRateBottomSheetBinding.inflate(inflater, container, boolean)
    }

    override fun initMain() {
        initView()
        initEvent()
    }

    private fun initView() {}

    private fun initEvent() {
        with(binding) {
            rbStar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
                rate = rating
            }

            btCancel.setOnClickListener {
                dismiss()
            }
            btSend.setOnClickListener {
                if (rate.toInt() == 5) {
                    openAppInGooglePlay()
                    Utils.toast(requireContext(), getString(R.string.text_rate_google_play))
                    dismiss()
                } else if (rate == 0f) {
                    Utils.toast(requireContext(), getString(R.string.text_need_feedback))
                } else {
                    sendFeedback(rate.toString())
                    dismiss()
                }
            }
        }
    }

    private fun openAppInGooglePlay() {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(LINK_GOOGLE_PLAY + requireContext().packageName)
            intent.setPackage(SET_PACKAGE)
            startActivity(intent)
        } catch (e: Exception) {
        }
    }

    private fun sendFeedback(text: String) {
        kotlin.runCatching {
            val deviceName = Build.MODEL
            val deviceMan = Build.MANUFACTURER
            val androidV = Build.VERSION.SDK_INT
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse(MailTo.MAILTO_SCHEME)
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(EMAIL))
            intent.putExtra(Intent.EXTRA_SUBJECT,
                getString(R.string.text_feedback_from_app, getString(R.string.app_name)))
            intent.putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.text_email_device) + deviceMan + " - " + deviceName + "\n"
                        + getString(R.string.text_email_sdk) + androidV + "\n\n" + text
            )
            startActivity(Intent.createChooser(intent, getString(R.string.text_choose_tools)))
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = RateBottomSheet()
    }
}