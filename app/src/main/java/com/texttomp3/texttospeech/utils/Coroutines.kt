package com.texttomp3.texttospeech.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object Coroutines {
    fun io(work: suspend (() -> Unit)) = CoroutineScope(Dispatchers.IO).launch {
        kotlin.runCatching {
            work()
        }.onFailure {
            Utils.log("Coroutine IO", it.message.toString())
        }
    }

    fun default(work: suspend (() -> Unit)) = CoroutineScope(Dispatchers.Default).launch {
        kotlin.runCatching {
            work()
        }.onFailure {
            Utils.log("Coroutine Default", it.message.toString())
        }
    }

    fun main(work: suspend (() -> Unit)) = CoroutineScope(Dispatchers.Main).launch {
        kotlin.runCatching {
            work()
        }.onFailure {
            Utils.log("Coroutine Main", it.message.toString())
        }
    }
}