package com.texttomp3.texttospeech.ui.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.texttomp3.texttospeech.data.models.Language
import com.texttomp3.texttospeech.adapters.LanguageAdapter
import com.texttomp3.texttospeech.base.BaseBottomSheetFragment
import com.texttomp3.texttospeech.data.models.LanguageItem
import com.texttomp3.texttospeech.databinding.FragmentLanguageBottomSheetBinding
import com.texttomp3.texttospeech.helpers.LanguageHelper
import com.texttomp3.texttospeech.utils.Utils

class LanguageBottomSheet(private val listener: OnSelectLanguageListener, private var languages: List<Language>) : BaseBottomSheetFragment<FragmentLanguageBottomSheetBinding>(),
    LanguageAdapter.OnClickListener {
    private lateinit var adapter: LanguageAdapter
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        boolean: Boolean
    ): FragmentLanguageBottomSheetBinding {
        return FragmentLanguageBottomSheetBinding.inflate(inflater, container, boolean)
    }

    override fun initMain() {
        initView()
        initEvent()
    }

    private fun initView() {
        with(binding) {
            val languageConfigs = LanguageHelper(binding.root.context).getLanguageList()
            val codeToNameMap = languageConfigs.associate { it.code to it.name }

            adapter = LanguageAdapter(this@LanguageBottomSheet)
            rvLanguage.adapter = adapter
            rvLanguage.layoutManager = LinearLayoutManager(requireContext())

            val sortedLanguages = languages.sortedWith(
                compareByDescending<Language> { it.isSelected }
                    .thenBy { codeToNameMap[it.name] ?: "" }
            )

            adapter.submitData(sortedLanguages)
        }
    }

    private fun initEvent() {
        with(binding) {
            ivBack.setOnClickListener {
                dismiss()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(listener: OnSelectLanguageListener, languages: List<Language>) = LanguageBottomSheet(listener, languages)
    }

    override fun onClick(language: Language) {
        for (l in languages) {
            l.isSelected = l.name == language.name
        }
        adapter.submitData(languages)
        listener.onSelectLanguage(language)
        dismiss()
    }

    interface OnSelectLanguageListener {
        fun onSelectLanguage(language: Language)
    }
}