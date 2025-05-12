package com.texttomp3.texttospeech.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.texttomp3.texttospeech.data.models.Project
import com.texttomp3.texttospeech.data.models.Sort
import com.texttomp3.texttospeech.utils.Constants.A_Z
import com.texttomp3.texttospeech.utils.Constants.LAST_MODIFIED
import com.texttomp3.texttospeech.utils.Constants.NAME
import com.texttomp3.texttospeech.utils.Constants.NEW_TO_OLD
import com.texttomp3.texttospeech.utils.Constants.OLD_TO_NEW
import com.texttomp3.texttospeech.utils.Constants.Z_A
import com.texttomp3.texttospeech.repositories.HomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(private val homeRepository: HomeRepository) : ViewModel() {
    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects

    private val _project = MutableStateFlow<Project?>(null)
    val project: StateFlow<Project?> = _project

    private val _sorts = MutableStateFlow(
        listOf(
            Sort(LAST_MODIFIED, NEW_TO_OLD, true),
            Sort(LAST_MODIFIED, OLD_TO_NEW, false),
            Sort(NAME, A_Z, false),
            Sort(NAME, Z_A, false)
        )
    )
    val sorts: StateFlow<List<Sort>> = _sorts

    fun getProjects() {
        viewModelScope.launch {
            val selectedSort = _sorts.value.find { it.isSelected } ?: _sorts.value.first()
            val queryProjects = homeRepository.getProjectsSorted(selectedSort)
            withContext(Dispatchers.Main) {
                _projects.value = queryProjects
            }
        }
    }

    suspend fun getProject(id: Long): Project {
        return homeRepository.getProject(id)
    }

    fun setSortType(type: String) {
        val updatedSorts = _sorts.value.map {
            it.copy(isSelected = it.subtitle == type)
        }
        _sorts.value = updatedSorts
        getProjects()
    }

    fun updateProject(project: Project): Boolean {
        var isSuccess = true
        viewModelScope.launch {
            val isNameDuplicated = _projects.value.any { it.name == project.name }

            if (isNameDuplicated) {
                isSuccess = false
            } else {
                _projects.value = _projects.value.map {
                    if (it.id == project.id) {
                        project
                    } else {
                        it
                    }
                }
                homeRepository.updateProject(project)
            }
        }
        return isSuccess
    }

    fun deleteProject(project: Project) {
        viewModelScope.launch {
            _projects.value = _projects.value.filter { it.id != project.id }
            homeRepository.deleteProject(project)
        }
    }

}