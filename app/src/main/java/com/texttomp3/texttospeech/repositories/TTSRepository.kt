package com.texttomp3.texttospeech.repositories

import com.texttomp3.texttospeech.data.models.Language
import com.texttomp3.texttospeech.data.models.Project
import com.texttomp3.texttospeech.data.models.Voice

interface TTSRepository {
    suspend fun generate(
        mode: String,
        language: String,
        voice: String,
        gender: String,
        displayIndex: Int,
        pitch: Float,
        speed: Float,
        volume: Float,
        text: String
    ): Project

    suspend fun reGenerate(
        id: Long,
        mode: String,
        language: String,
        voice: String,
        gender: String,
        displayIndex: Int,
        pitch: Float,
        speed: Float,
        volume: Float,
        text: String
    ): String

    suspend fun getLanguages(mode: String): List<Language>

    suspend fun getVoices(
        mode: String,
        language: String
    ): List<Voice>

    fun setSetting(
        mode: String,
        language: String,
        voice: Voice,
        pitch: Float,
        speed: Float,
        volume: Float
    )
}