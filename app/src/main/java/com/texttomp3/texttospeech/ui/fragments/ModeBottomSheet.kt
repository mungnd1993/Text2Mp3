package com.texttomp3.texttospeech.ui.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.texttomp3.texttospeech.data.models.Mode
import com.texttomp3.texttospeech.adapters.ModeAdapter
import com.texttomp3.texttospeech.base.BaseBottomSheetFragment
import com.texttomp3.texttospeech.databinding.FragmentModeBottomSheetBinding
import com.texttomp3.texttospeech.utils.Constants.ANDROID
import com.texttomp3.texttospeech.utils.Constants.EDGE

class ModeBottomSheet(private val listener: OnSelectModeListener, private val mode: String) : BaseBottomSheetFragment<FragmentModeBottomSheetBinding>(),
    ModeAdapter.OnClickListener {
    private val modes = mutableListOf(Mode(ANDROID, true), Mode(EDGE, false))
    private lateinit var adapter: ModeAdapter

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        boolean: Boolean
    ): FragmentModeBottomSheetBinding {
        return FragmentModeBottomSheetBinding.inflate(inflater, container, boolean)
    }

    override fun initMain() {
        initView()
        initEvent()
    }

    private fun initView() {
        with(binding) {
            adapter = ModeAdapter(this@ModeBottomSheet)
            rvMode.adapter = adapter
            rvMode.layoutManager = LinearLayoutManager(requireContext())
            for (m in modes) {
                m.isSelected = m.name == mode
            }
            adapter.submitData(modes)
        }
    }

    private fun initEvent() {
        with(binding) {
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(listener: OnSelectModeListener, mode: String) = ModeBottomSheet(listener, mode)
    }

    override fun onClick(mode: Mode) {
        for (m in modes) {
            m.isSelected = m.name == mode.name
        }
        adapter.submitData(modes)
        listener.onSelectMode(mode)
        dismiss()
    }

    interface OnSelectModeListener {
        fun onSelectMode(mode: Mode)
    }
}