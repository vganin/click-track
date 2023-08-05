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

fun MediaFormat.channelCount(): Int = getInteger(MediaFormat.KEY_CHANNEL_COUNT)

fun MediaFormat.sampleRate(): Int = getInteger(MediaFormat.KEY_SAMPLE_RATE)

fun MediaFormat.bitDepth(): Int = AudioFormatHelper.pcmEncodingToBitDepth(pcmEncoding())

fun MediaFormat.bytesPerSample(): Int = AudioFormatHelper.bytesPerSample(bitDepth())

fun MediaFormat.bytesPerSecond(): Int = sampleRate() * bytesPerSample() * channelCount()

private fun <T> MediaFormat.getOptional(key: String, getter: MediaFormat.(String) -> T): T? {
    return if (containsKey(key)) {
        getter.invoke(this, key)
    } else {
        null
    }
}
