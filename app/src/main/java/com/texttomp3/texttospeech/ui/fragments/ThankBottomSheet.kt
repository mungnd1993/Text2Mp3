package com.texttomp3.texttospeech.ui.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import com.texttomp3.texttospeech.base.BaseBottomSheetFragment
import com.texttomp3.texttospeech.databinding.FragmentThankBottomSheetBinding
import com.texttomp3.texttospeech.utils.Constants.RATE_BOTTOM_SHEET

class ThankBottomSheet : BaseBottomSheetFragment<FragmentThankBottomSheetBinding>() {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        boolean: Boolean
    ): FragmentThankBottomSheetBinding {
        return FragmentThankBottomSheetBinding.inflate(inflater, container, boolean)
    }

    override fun initMain() {
        initView()
        initEvent()
    }

    private fun initView() {}

    private fun initEvent() {
        with(binding) {
            btOk.setOnClickListener {
                dismiss()
                val rateBottomSheet = RateBottomSheet.newInstance()
                rateBottomSheet.show(parentFragmentManager, RATE_BOTTOM_SHEET)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ThankBottomSheet()
    }
}