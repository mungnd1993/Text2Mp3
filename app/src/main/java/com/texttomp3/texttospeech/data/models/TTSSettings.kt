package com.texttomp3.texttospeech.data.models

data class TTSSettings(
    val mode: String,
    val language: String,
    val voice: Voice,
    val pitch: Float,
    val speed: Float,
    val volume: Float
)
