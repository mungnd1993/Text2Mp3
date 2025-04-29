package com.texttomp3.texttospeech.ui.fragments

import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import com.texttomp3.texttospeech.R
import com.texttomp3.texttospeech.base.BaseFragment
import com.texttomp3.texttospeech.databinding.FragmentWelcomeBinding
import com.texttomp3.texttospeech.utils.Constants.LINK_PRIVACY
import com.texttomp3.texttospeech.utils.Constants.LINK_TERM
import androidx.core.net.toUri

class WelcomeFragment : BaseFragment<FragmentWelcomeBinding>() {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        boolean: Boolean
    ): FragmentWelcomeBinding {
        return FragmentWelcomeBinding.inflate(inflater, container, boolean)
    }

    override fun initMain() {
        initView()
        initEvent()
    }

    private fun initView() {
        val textPolicy = getString(R.string.policy)
        val spannableString2 = SpannableString(textPolicy)
        spannableString2.setSpan(UnderlineSpan(), 0, textPolicy.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val textTerm = getString(R.string.terms_of_use)
        val spannableString3 = SpannableString(textTerm)
        spannableString3.setSpan(UnderlineSpan(), 0, textTerm.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        with(binding) {
            tvPolicy.text = spannableString2
            tvTerm.text = spannableString3
        }

    }

    private fun initEvent() {
        with(binding) {
            btContinue.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fcv_main, IntroFragment.newInstance())
                    .commit()
            }

            tvPolicy.setOnClickListener {
                startActivity(
                    Intent(Intent.ACTION_VIEW, LINK_PRIVACY.toUri())
                )
            }

            tvTerm.setOnClickListener {
                startActivity(
                    Intent(Intent.ACTION_VIEW, LINK_TERM.toUri())
                )
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = WelcomeFragment()
    }


}