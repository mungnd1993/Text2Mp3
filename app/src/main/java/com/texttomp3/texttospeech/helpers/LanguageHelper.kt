package com.texttomp3.texttospeech.helpers

import android.content.Context
import com.texttomp3.texttospeech.data.models.LanguageItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class LanguageHelper(context: Context) {
    private val languageList = arrayListOf<LanguageItem>()

    init {
        fun getJsonFromAsset(context: Context, filename: String): String {
            val stringBuilder = StringBuilder()
            try {
                val inputStream = context.assets.open(filename)
                val bufferedReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))

                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) {
                    stringBuilder.append(line)
                }

                bufferedReader.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return stringBuilder.toString()
        }

        val languageItemStr = getJsonFromAsset(context, "language.json")
        val type = object : TypeToken<MutableList<LanguageItem>>() {}.type
        val mutableList = Gson().fromJson<MutableList<LanguageItem>>(languageItemStr, type)
        languageList.addAll(mutableList)
    }

    fun getLanguageList() = languageList
}