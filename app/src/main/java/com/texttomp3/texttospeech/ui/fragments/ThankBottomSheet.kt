package com.texttomp3.texttospeech.ui.fragments

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import com.texttomp3.texttospeech.base.BaseBottomSheetFragment
import com.texttomp3.texttospeech.databinding.FragmentThankBottomSheetBinding
import com.texttomp3.texttospeech.ui.activities.MainActivity
import com.texttomp3.texttospeech.utils.Constants.RATE_BOTTOM_SHEET
import com.texttomp3.texttospeech.viewmodels.SettingViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel

class ThankBottomSheet : BaseBottomSheetFragment<FragmentThankBottomSheetBinding>() {
    private val settingViewModel: SettingViewModel by lazy {
        getViewModel()
    }

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
                settingViewModel.reset()
                dismiss()
//                val rateBottomSheet = RateBottomSheet.newInstance()
//                rateBottomSheet.show(parentFragmentManager, RATE_BOTTOM_SHEET)
                requireActivity().finish()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ThankBottomSheet()
    }
}