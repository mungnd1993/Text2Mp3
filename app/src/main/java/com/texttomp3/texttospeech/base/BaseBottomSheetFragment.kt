package com.texttomp3.texttospeech.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetFragment<VB: ViewBinding>: BottomSheetDialogFragment() {
    private var _binding: VB? = null
    val binding get() = _binding!!

    abstract fun createBinding(inflater: LayoutInflater, container: ViewGroup?, boolean: Boolean): VB

    abstract fun initMain()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setDimAmount(0.6f)
        _binding = createBinding(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMain()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}