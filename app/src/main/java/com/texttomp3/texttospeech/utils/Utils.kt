package com.texttomp3.texttospeech.utils

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.FragmentManager
import com.texttomp3.texttospeech.data.models.TTSSettings
import com.texttomp3.texttospeech.data.models.Voice
import com.texttomp3.texttospeech.helpers.PreferenceHelper
import com.texttomp3.texttospeech.ui.fragments.LoadingFragment
import com.texttomp3.texttospeech.utils.Constants.ANDROID
import com.texttomp3.texttospeech.utils.Constants.DISPLAY_INDEX
import com.texttomp3.texttospeech.utils.Constants.GB
import com.texttomp3.texttospeech.utils.Constants.GENDER
import com.texttomp3.texttospeech.utils.Constants.LANGUAGE
import com.texttomp3.texttospeech.utils.Constants.MODE
import com.texttomp3.texttospeech.utils.Constants.PITCH
import com.texttomp3.texttospeech.utils.Constants.SPEED
import com.texttomp3.texttospeech.utils.Constants.VOICE
import com.texttomp3.texttospeech.utils.Constants.VOICE_DEFAULT
import com.texttomp3.texttospeech.utils.Constants.VOLUME
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.graphics.toColorInt

object Utils {
    fun log(content: String) {
        Log.d("TTS", content)
    }

    fun log(tag: String, content: String) {
        Log.d(tag, content)
    }

    fun toast(context: Context, content: String) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
    }

    /** Set gradient text to TextView */
    fun setGradientText(tv: AppCompatTextView) {
        tv.viewTreeObserver.addOnPreDrawListener {
            val width = tv.width.toFloat()

            val linearGradient = LinearGradient(
                0f, 0f, width, 0f,
                "#0E2BC5".toColorInt(),
                "#9000FF".toColorInt(),
                Shader.TileMode.CLAMP
            )

            tv.paint.shader = linearGradient
            true
        }
    }

    /**
     * Show loading fragment when action is processing
     */
    fun showLoadingFragment(
        fragmentManager: FragmentManager,
        containerId: Int,
        message: String
    ): LoadingFragment {
        val loadingFragment = LoadingFragment.newInstance(message)
        fragmentManager.beginTransaction()
            .add(containerId, loadingFragment)
            .addToBackStack(null)
            .commit()
        return loadingFragment
    }

    /**
     * Convert timestamp to date
     */
    fun formatToDate(input: Long): String {
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = Date(input)
        return outputFormat.format(date)
    }

    /**
     * Convert timestamp to hour and minute
     */
    fun formatToTime(input: Long): String {
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = Date(input)
        return outputFormat.format(date)
    }

    /**
     * Format duration project TTSActivity
     */
    @SuppressLint("DefaultLocale")
    fun convertDuration(time: Int): String {
        val minutes = time / 1000 / 60
        val seconds = (time / 1000) % 60

        return String.format("%02d:%02d", minutes, seconds)
    }

    fun Float.toCommaString(): String {
        val symbols = DecimalFormatSymbols().apply { decimalSeparator = ',' }
        val df = DecimalFormat("#0.0#", symbols)
        return df.format(this)
    }

    /**
     * Format size project in MoreBottomSheet
     */
    fun formatSize(sizeInBytes: Long): String {
        val kb = sizeInBytes / 1024.0
        val mb = kb / 1024.0
        return when {
            mb >= 1 -> "${mb.toInt()}mb"
            kb >= 1 -> "${kb.toInt()}kb"
            else -> "$sizeInBytes B"
        }
    }

    /**
     * Download file
     */
    fun saveMp3ToMediaStore(context: Context, file: File): Boolean {
        val contentResolver = context.contentResolver
        val fileName = file.nameWithoutExtension

        val contentValues = ContentValues().apply {
            put(MediaStore.Audio.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Audio.Media.MIME_TYPE, "audio/mpeg")
            put(MediaStore.Audio.Media.RELATIVE_PATH, "Music/TextToMP3") // Thư mục trong Music
            put(MediaStore.Audio.Media.IS_PENDING, 1)
        }

        val uri = contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues)

        return if (uri != null) {
            try {
                contentResolver.openOutputStream(uri).use { outputStream ->
                    if (outputStream != null) {
                        FileInputStream(file).use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    } else {
                        return false
                    }
                }

                contentValues.clear()
                contentValues.put(MediaStore.Audio.Media.IS_PENDING, 0)
                contentResolver.update(uri, contentValues, null, null)

                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        } else {
            false
        }
    }


    /**
     * Get saved settings from SharedPreferences
     */
    fun getSavedSettings(context: Context): TTSSettings {
        val preferenceHelper = PreferenceHelper.getInstance(context)

        val savedMode = preferenceHelper.getString(MODE, ANDROID)
        val savedLanguage = preferenceHelper.getString(LANGUAGE, GB)
        val savedVoice = preferenceHelper.getString(VOICE, VOICE_DEFAULT)
        val savedGender = preferenceHelper.getString(GENDER, "")
        val savedIndex = preferenceHelper.getInt(DISPLAY_INDEX, 1)
        val savedPitch = preferenceHelper.getFloat(PITCH, 1.0f)
        val savedSpeed = preferenceHelper.getFloat(SPEED, 1.0f)
        val savedVolume = preferenceHelper.getFloat(VOLUME, 0.5f)

        return TTSSettings(
            savedMode,
            savedLanguage,
            Voice(savedVoice, savedGender, isSelected = true, isPlaying = false, savedIndex),
            savedPitch,
            savedSpeed,
            savedVolume
        )
    }

    fun getAppVersion(context: Context): String {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val versionName = packageInfo.versionName
        val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            packageInfo.versionCode.toLong()
        }
        return "$versionName"
    }


}