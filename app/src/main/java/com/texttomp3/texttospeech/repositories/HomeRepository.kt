package com.texttomp3.texttospeech.repositories

import com.texttomp3.texttospeech.data.models.Project
import com.texttomp3.texttospeech.data.models.Sort

interface HomeRepository {
    suspend fun getProjects(): List<Project>

    suspend fun getProjectsSorted(sort: Sort): List<Project>

    suspend fun getProject(id: Long): Project

    suspend fun updateProject(project: Project)

    suspend fun deleteProject(project: Project)
}