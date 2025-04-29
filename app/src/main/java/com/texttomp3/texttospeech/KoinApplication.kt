package com.texttomp3.texttospeech

import android.app.Application
import com.texttomp3.texttospeech.di.repositoryModule
import com.texttomp3.texttospeech.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

open class KoinApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@KoinApplication)
            modules(listOf(repositoryModule, viewModelModule))
        }
    }
}