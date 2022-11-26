package com.vsevolodganin.clicktrack.utils.media

import android.media.AudioFormat
import android.media.MediaFormat
import android.os.Build

fun MediaFormat.pcmEncoding(): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        getOptional(MediaFormat.KEY_PCM_ENCODING, MediaFormat::getInteger) ?: AudioFormat.ENCODING_PCM_16BIT
    } else {
        AudioFormat.ENCODING_PCM_16BIT
    }
}

fun MediaFormat.channelCount(): Int {
    return getInteger(MediaFormat.KEY_CHANNEL_COUNT)
}

fun MediaFormat.sampleRate(): Int {
    return getInteger(MediaFormat.KEY_SAMPLE_RATE)
}

fun MediaFormat.bytesPerSample(): Int {
    return when (val pcmEncoding = pcmEncoding()) {
        AudioFormat.ENCODING_PCM_8BIT -> 1
        AudioFormat.ENCODING_PCM_16BIT -> 2
        AudioFormat.ENCODING_PCM_24BIT_PACKED -> 3
        AudioFormat.ENCODING_PCM_32BIT,
        AudioFormat.ENCODING_PCM_FLOAT,
        -> 4
        AudioFormat.ENCODING_INVALID -> throw IllegalArgumentException("Bad audio format $pcmEncoding")
        else -> throw IllegalArgumentException("Bad audio format $pcmEncoding")
    }
}

fun MediaFormat.bytesPerSecond(): Int {
    return sampleRate() * bytesPerSample() * channelCount()
}

private fun <T> MediaFormat.getOptional(key: String, getter: MediaFormat.(String) -> T): T? {
    return if (containsKey(key)) {
        getter.invoke(this, key)
    } else {
        null
    }
}
