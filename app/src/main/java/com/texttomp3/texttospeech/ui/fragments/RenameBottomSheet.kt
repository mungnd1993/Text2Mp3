package com.texttomp3.texttospeech.ui.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import com.texttomp3.texttospeech.R
import com.texttomp3.texttospeech.data.models.Project
import com.texttomp3.texttospeech.base.BaseBottomSheetFragment
import com.texttomp3.texttospeech.databinding.FragmentRenameBottomSheetBinding
import com.texttomp3.texttospeech.utils.Utils
import com.texttomp3.texttospeech.viewmodels.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel

class RenameBottomSheet(private val project: Project) : BaseBottomSheetFragment<FragmentRenameBottomSheetBinding>() {

    private val homeViewModel: HomeViewModel by lazy {
        requireActivity().getViewModel<HomeViewModel>()
    }

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        boolean: Boolean
    ): FragmentRenameBottomSheetBinding {
        return FragmentRenameBottomSheetBinding.inflate(inflater, container, boolean)
    }

    override fun initMain() {
        initView()
        initEvent()
    }

    private fun initView() {
        with(binding) {
            etName.setText(project.name)
        }
    }

    private fun initEvent() {
        with(binding) {
            ivClear.setOnClickListener {
                etName.text?.clear()
            }

            btCancel.setOnClickListener {
                dismiss()
            }

            btSave.setOnClickListener {
                if (etName.text.isNullOrEmpty()) {
                    Utils.toast(requireContext(), requireContext().getString(R.string.name_is_not_null))
                } else {
                    val result = homeViewModel.updateProject(project.copy(name = etName.text.toString(), time = System.currentTimeMillis()))
                    if (result) {
                        Utils.toast(requireContext(), requireContext().getString(R.string.rename_success))
                        dismiss()
                    } else {
                        Utils.toast(requireContext(), requireContext().getString(R.string.name_duplicate))
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(project: Project) = RenameBottomSheet(project)
    }
}