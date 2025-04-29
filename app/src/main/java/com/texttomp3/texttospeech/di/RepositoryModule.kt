package com.texttomp3.texttospeech.di

import com.texttomp3.texttospeech.repositories.HomeRepository
import com.texttomp3.texttospeech.repositories.HomeRepositoryImpl
import com.texttomp3.texttospeech.repositories.SettingRepository
import com.texttomp3.texttospeech.repositories.SettingRepositoryImpl
import com.texttomp3.texttospeech.repositories.TTSRepository
import com.texttomp3.texttospeech.repositories.TTSRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    factory<HomeRepository> { HomeRepositoryImpl(androidContext()) }
    factory<TTSRepository> { TTSRepositoryImpl(androidContext()) }
    factory<SettingRepository> { SettingRepositoryImpl(androidContext()) }
}