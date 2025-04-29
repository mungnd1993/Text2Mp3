package com.texttomp3.texttospeech.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.texttomp3.texttospeech.data.models.Language
import com.texttomp3.texttospeech.data.models.TTSSettings
import com.texttomp3.texttospeech.data.models.Voice
import com.texttomp3.texttospeech.utils.Constants.ANDROID
import com.texttomp3.texttospeech.utils.Constants.GB
import com.texttomp3.texttospeech.utils.Constants.VOICE_DEFAULT
import com.texttomp3.texttospeech.utils.Coroutines
import com.texttomp3.texttospeech.repositories.TTSRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TTSViewModel(private val ttsRepository: TTSRepository) : ViewModel() {

    private val _currentSetting = MutableStateFlow(
        TTSSettings(
            ANDROID,
            GB,
            Voice(VOICE_DEFAULT, "", isSelected = true, isPlaying = false, 1),
            1.0f,
            1.0f,
            0.5f
        )
    )
    val currentSetting: StateFlow<TTSSettings> = _currentSetting

    private val _showSetting = MutableStateFlow(
        TTSSettings(
            ANDROID,
            GB,
            Voice(VOICE_DEFAULT, "",  isSelected = true, isPlaying = false, 1),
            1.0f,
            1.0f,
            0.5f
        )
    )
    val showSetting: StateFlow<TTSSettings> = _showSetting

    private val _languages = MutableStateFlow<List<Language>>(emptyList())
    val languages: StateFlow<List<Language>> = _languages

    private val _voices = MutableStateFlow<List<Voice>>(emptyList())
    val voices: StateFlow<List<Voice>> = _voices

    private val _id = MutableStateFlow(0L)
    val id: StateFlow<Long> = _id

    private val _text = MutableStateFlow("")
    val text: StateFlow<String> = _text

    private val _path = MutableStateFlow("")
    val path: StateFlow<String> = _path

    fun init(
        mode: String,
        language: String,
        voice: Voice,
        pitch: Float,
        speed: Float,
        volume: Float
    ) {
        _currentSetting.value = _currentSetting.value.copy(
            mode = mode,
            language = language,
            voice = voice,
            pitch = pitch,
            speed = speed,
            volume = volume
        )
        resetLanguagesAndVoices(0)
    }

    fun setTextAndPath(id: Long, text: String, path: String) {
        _id.value = id
        _text.value = text
        _path.value = path
    }

    fun cloneSetting() {
        _showSetting.value = _currentSetting.value
        resetLanguagesAndVoices(0)
    }

    fun setShowSetting(
        mode: String?,
        language: String?,
        voice: Voice?,
        pitch: Float?,
        speed: Float?,
        volume: Float?
    ) {
        if (mode != null && mode != _showSetting.value.mode) {
            _showSetting.value = _showSetting.value.copy(
                mode = mode
            )
            resetLanguagesAndVoices(1)
        }
        _showSetting.value = _showSetting.value.copy(
            mode = mode ?: _showSetting.value.mode,
            language = language ?: _showSetting.value.language,
            voice = voice ?: _showSetting.value.voice,
            pitch = pitch ?: _showSetting.value.pitch,
            speed = speed ?: _showSetting.value.speed,
            volume = volume ?: _showSetting.value.volume
        )
        when {
            language != null -> {
                resetLanguagesAndVoices(2)
            }

            voice != null -> {
                resetLanguagesAndVoices(3)
            }
        }
    }

    fun setCurrentSetting() {
        _currentSetting.value = _showSetting.value
        ttsRepository.setSetting(
            _currentSetting.value.mode,
            _currentSetting.value.language,
            _currentSetting.value.voice,
            _currentSetting.value.pitch,
            _currentSetting.value.speed,
            _currentSetting.value.volume
        )
    }

    fun generate(text: String) {
        Coroutines.io {
            if (_path.value == "") {
                val project = ttsRepository.generate(
                    _currentSetting.value.mode,
                    _currentSetting.value.language,
                    _currentSetting.value.voice.name,
                    _currentSetting.value.voice.gender,
                    _currentSetting.value.voice.displayIndex,
                    _currentSetting.value.pitch,
                    _currentSetting.value.speed,
                    _currentSetting.value.volume,
                    text
                )

                withContext(IO) {
                    _id.value = project.id
                    _text.value = project.content
                    _path.value = project.filePath
                }
            } else {
                val generatedPath = ttsRepository.reGenerate(
                    _id.value,
                    _currentSetting.value.mode,
                    _currentSetting.value.language,
                    _currentSetting.value.voice.name,
                    _currentSetting.value.voice.gender,
                    _currentSetting.value.voice.displayIndex,
                    _currentSetting.value.pitch,
                    _currentSetting.value.speed,
                    _currentSetting.value.volume,
                    text
                )

                withContext(IO) {
                    _path.value = generatedPath
                }
            }
        }
    }

    fun resetPath() {
        _id.value = 0
        _path.value = ""
        _text.value = ""
    }

    fun getVoices(): List<Voice> {
        return _voices.value
    }

    fun resetLanguagesAndVoices(type: Int) {
        viewModelScope.launch {
            when (type) {
                0 -> {
                    _languages.value = ttsRepository.getLanguages(_currentSetting.value.mode)
                    _voices.value = ttsRepository.getVoices(
                        _currentSetting.value.mode,
                        _currentSetting.value.language
                    )
                    for (item in _languages.value) {
                        item.isSelected = item.name == _currentSetting.value.language
                    }
                    for (item in _voices.value) {
                        item.isSelected = item.name == _currentSetting.value.voice.name
                    }
                    _languages.value = _languages.value.sortedByDescending { it.isSelected }
                    _voices.value = _voices.value.sortedByDescending { it.isSelected }
                }

                1 -> {
                    _languages.value = ttsRepository.getLanguages(_showSetting.value.mode)
                    _languages.value =
                        _languages.value.sortedByDescending { it.name == GB }
                    if (_languages.value.isNotEmpty()) {
                        _languages.value[0].isSelected = true
                        _showSetting.value = _showSetting.value.copy(
                            language = _languages.value[0].name
                        )
                        resetLanguagesAndVoices(2)
                    }
                }

                2 -> {
                    _languages.value = ttsRepository.getLanguages(_showSetting.value.mode)
                    _voices.value =
                        ttsRepository.getVoices(_showSetting.value.mode, _showSetting.value.language)
                    for (item in _languages.value) {
                        item.isSelected = item.name == _showSetting.value.language
                    }
                    if (_voices.value.isNotEmpty()) {
                        _voices.value[0].isSelected = true
                        _showSetting.value = _showSetting.value.copy(
                            voice = Voice(_voices.value[0].name, _voices.value[0].gender, isSelected = true, isPlaying = false, _voices.value[0].displayIndex)
                        )
                    }
                    _languages.value = _languages.value.sortedByDescending { it.isSelected }
                    _voices.value = _voices.value.sortedByDescending { it.isSelected }
                }

                else -> {
                    _languages.value = ttsRepository.getLanguages(_showSetting.value.mode)
                    _voices.value =
                        ttsRepository.getVoices(_showSetting.value.mode, _showSetting.value.language)
                    for (item in _languages.value) {
                        item.isSelected = item.name == _showSetting.value.language
                    }
                    for (item in _voices.value) {
                        item.isSelected = item.name == _showSetting.value.voice.name
                    }
                    _languages.value = _languages.value.sortedByDescending { it.isSelected }
                    _voices.value = _voices.value.sortedByDescending { it.isSelected }
                }
            }
        }
    }
}