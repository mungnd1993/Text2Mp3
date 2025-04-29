package com.texttomp3.texttospeech.data.rooms

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.texttomp3.texttospeech.data.models.Project

@Database(entities = [Project::class], version = 1)
abstract class ProjectDatabase: RoomDatabase() {
    abstract fun projectDao(): ProjectDAO

    companion object {
        private var instance: ProjectDatabase? = null

        fun getInstance(context: Context): ProjectDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProjectDatabase::class.java,
                    "tts_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance!!
        }
    }
}