package com.texttomp3.texttospeech.data.rooms

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.texttomp3.texttospeech.data.models.Project

@Dao
interface ProjectDAO {
    @Query("SELECT * FROM project")
    suspend fun getProjects(): List<Project>

    @Query("SELECT * FROM Project ORDER BY time DESC")
    suspend fun getProjectsOrderByLastModifiedDesc(): List<Project>

    @Query("SELECT * FROM Project ORDER BY time ASC")
    suspend fun getProjectsOrderByLastModifiedAsc(): List<Project>

    @Query("SELECT * FROM Project ORDER BY name COLLATE NOCASE ASC")
    suspend fun getProjectsOrderByNameAsc(): List<Project>

    @Query("SELECT * FROM Project ORDER BY name COLLATE NOCASE DESC")
    suspend fun getProjectsOrderByNameDesc(): List<Project>

    @Query("SELECT * FROM project WHERE id = :id")
    suspend fun getProject(id: Long): Project

    @Query("SELECT * FROM project ORDER BY time DESC LIMIT 1")
    suspend fun getNewestProject(): Project

    @Insert
    suspend fun insertProject(project: Project): Long

    @Update
    suspend fun updateProject(project: Project)

    @Delete
    suspend fun deleteProject(project: Project)
}