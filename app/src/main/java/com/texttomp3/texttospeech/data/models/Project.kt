package com.texttomp3.texttospeech.data.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "project")
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val mode: String,
    val language: String,
    val voice: String,
    val gender: String,
    val displayIndex: Int,
    var content: String,
    var filePath: String,
    val pitch: Float,
    val speed: Float,
    val volume: Float,
    val time: Long
) : Parcelable
