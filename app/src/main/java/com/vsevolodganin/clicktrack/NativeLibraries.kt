package com.vsevolodganin.clicktrack

import android.content.Context
import android.media.AudioManager
import androidx.core.content.getSystemService

object NativeLibraries {

    fun init(context: Context) {
        System.loadLibrary("oboe")
        System.loadLibrary("clicktrack")

        initOboeDefaults(context)
    }

    private fun initOboeDefaults(context: Context) {
        val audioManaager = context.getSystemService<AudioManager>()!!
        val defaultSampleRate = audioManaager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE).toInt()
        val defaultFramesPerBurst = audioManaager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER).toInt()
        initOboeDefaults(defaultSampleRate, defaultFramesPerBurst)
    }

    private external fun initOboeDefaults(defaultSampleRate: Int, defaultFramesPerBurst: Int)
}
