package com.texttomp3.texttospeech.viewmodels

import androidx.lifecycle.ViewModel
import com.texttomp3.texttospeech.data.models.Problem
import com.texttomp3.texttospeech.repositories.SettingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingViewModel(private val settingRepository: SettingRepository) : ViewModel() {
    private val _problems = MutableStateFlow(
        listOf(
            Problem("User interface", false),
            Problem("Private Folder", false),
            Problem("Notification", false),
            Problem("Support", false),
            Problem("Language", false),
            Problem("Other", false),
        )
    )
    val problems: StateFlow<List<Problem>> = _problems

    private val _detail = MutableStateFlow("")
    val detail: StateFlow<String> = _detail

    private val _proVersion = MutableStateFlow(false)
    val proVersion: StateFlow<Boolean> = _proVersion

    fun setProblem(problem: String) {
        _problems.value = _problems.value.map {
            if (it.text == problem) it.copy(isSelection = !it.isSelection) else it
        }
    }

    fun setDetail(detail: String) {
        _detail.value = detail
    }

    fun setProVersion(proVersion: Boolean) {
        _proVersion.value = proVersion
    }

    fun reset() {
        _problems.value = _problems.value.map { it.copy(isSelection = false) }
        _detail.value = ""
    }
}