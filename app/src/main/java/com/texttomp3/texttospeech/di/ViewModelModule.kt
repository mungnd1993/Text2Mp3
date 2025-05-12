package com.texttomp3.texttospeech.di

import com.texttomp3.texttospeech.viewmodels.HomeViewModel
import com.texttomp3.texttospeech.viewmodels.SettingViewModel
import com.texttomp3.texttospeech.viewmodels.TTSViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(homeRepository = get()) }
    viewModel  { TTSViewModel(ttsRepository = get()) }
    single { SettingViewModel(settingRepository = get()) }
}