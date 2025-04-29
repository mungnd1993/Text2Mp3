package com.texttomp3.texttospeech.repositories

import android.content.Context
import com.texttomp3.texttospeech.data.models.Project
import com.texttomp3.texttospeech.data.models.Sort
import com.texttomp3.texttospeech.data.rooms.ProjectDatabase
import com.texttomp3.texttospeech.utils.Constants.A_Z
import com.texttomp3.texttospeech.utils.Constants.NEW_TO_OLD
import com.texttomp3.texttospeech.utils.Constants.OLD_TO_NEW
import com.texttomp3.texttospeech.utils.Constants.Z_A

class HomeRepositoryImpl(private val context: Context) : HomeRepository {
    private val db = ProjectDatabase.getInstance(context)
    private val projectDAO = db.projectDao()

    override suspend fun getProjects(): List<Project> {
        return projectDAO.getProjects()
    }

    override suspend fun getProjectsSorted(sort: Sort): List<Project> {
        return when (sort.subtitle) {
            NEW_TO_OLD -> projectDAO.getProjectsOrderByLastModifiedDesc()
            OLD_TO_NEW -> projectDAO.getProjectsOrderByLastModifiedAsc()
            A_Z -> projectDAO.getProjectsOrderByNameAsc()
            Z_A -> projectDAO.getProjectsOrderByNameDesc()
            else -> projectDAO.getProjects()
        }
    }

    override suspend fun getProject(id: Long): Project {
        return projectDAO.getProject(id)
    }

    override suspend fun updateProject(project: Project) {
        return projectDAO.updateProject(project)
    }

    override suspend fun deleteProject(project: Project) {
        return projectDAO.deleteProject(project)
    }

}