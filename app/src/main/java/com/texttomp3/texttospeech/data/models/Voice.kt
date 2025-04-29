package com.texttomp3.texttospeech.data.models

data class Voice(
    val name: String,
    val gender: String,
    var isSelected: Boolean,
    var isPlaying: Boolean,
    var displayIndex: Int = 0
)
