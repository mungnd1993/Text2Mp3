package com.texttomp3.texttospeech.ui.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import com.texttomp3.texttospeech.data.models.Project
import com.texttomp3.texttospeech.base.BaseBottomSheetFragment
import com.texttomp3.texttospeech.databinding.FragmentDeleteBottomSheetBinding
import com.texttomp3.texttospeech.ui.activities.MainActivity
import com.texttomp3.texttospeech.viewmodels.HomeViewModel
import com.texttomp3.texttospeech.viewmodels.TTSViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel

class DeleteBottomSheet(private val project: Project) : BaseBottomSheetFragment<FragmentDeleteBottomSheetBinding>() {
    private val homeViewModel: HomeViewModel by lazy {
        requireActivity().getViewModel<HomeViewModel>()
    }

    private val ttsViewModel: TTSViewModel by lazy {
        getViewModel<TTSViewModel>()
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        boolean: Boolean
    ): FragmentDeleteBottomSheetBinding {
        return FragmentDeleteBottomSheetBinding.inflate(inflater, container, boolean)
    }

    override fun initMain() {
        initView()
        initEvent()
    }

    private fun initView() {
        with(binding) {
            tvName.text = project.name
        }
    }

    private fun initEvent() {
        with(binding) {
            btCancel.setOnClickListener {
                dismiss()
            }
            btDelete.setOnClickListener {
                homeViewModel.deleteProject(project)
                if (requireActivity() !is MainActivity) {
                    ttsViewModel.resetPath()
                    requireActivity().finish()
                }
                dismiss()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(project: Project) = DeleteBottomSheet(project)
    }
}