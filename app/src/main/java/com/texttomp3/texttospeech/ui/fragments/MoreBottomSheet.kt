package com.texttomp3.texttospeech.ui.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.texttomp3.texttospeech.data.models.Project
import com.texttomp3.texttospeech.R
import com.texttomp3.texttospeech.base.BaseBottomSheetFragment
import com.texttomp3.texttospeech.databinding.FragmentMoreBottomSheetBinding
import com.texttomp3.texttospeech.utils.Constants.DELETE_BOTTOM_SHEET
import com.texttomp3.texttospeech.utils.Constants.RENAME_BOTTOM_SHEET
import com.texttomp3.texttospeech.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MoreBottomSheet(private val project: Project) : BaseBottomSheetFragment<FragmentMoreBottomSheetBinding>() {
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        boolean: Boolean
    ): FragmentMoreBottomSheetBinding {
        return FragmentMoreBottomSheetBinding.inflate(inflater, container, boolean)
    }

    override fun initMain() {
        initView()
        initEvent()
    }

    private fun initView() {
        with(binding) {
            ivImage.setCountryCode(project.language)
            tvFilename.text = project.name
            tvDate.text = Utils.formatToDate(project.time)
            tvCapacity.text = Utils.formatSize(File(project.filePath).length())
        }
    }

    private fun initEvent() {
        with(binding) {
            ivClose.setOnClickListener {
                dismiss()
            }

            clDownload.setOnClickListener {
                dialog?.window?.decorView?.visibility = View.GONE
                val loadingFragment = Utils.showLoadingFragment(
                    requireActivity().supportFragmentManager,
                    R.id.fcv_main2,
                    getString(R.string.downloading_audio)
                )

                lifecycleScope.launch {
                    val success = withContext(Dispatchers.IO) {
                        Utils.saveMp3ToMediaStore(requireContext(), File(project.filePath))
                    }
                    if (success) {
                        loadingFragment.showSuccessAndDismiss()
                        dismiss()
                    }
                }

            }


            clRename.setOnClickListener {
                dismiss()
                val renameBottomSheet = RenameBottomSheet.newInstance(project)
                renameBottomSheet.show(parentFragmentManager, RENAME_BOTTOM_SHEET)
            }

            clDelete.setOnClickListener {
                dismiss()
                val deleteBottomSheet = DeleteBottomSheet.newInstance(project)
                deleteBottomSheet.show(parentFragmentManager, DELETE_BOTTOM_SHEET)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(project: Project) = MoreBottomSheet(project)
    }

}