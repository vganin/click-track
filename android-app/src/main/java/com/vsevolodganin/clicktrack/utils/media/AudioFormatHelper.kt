package com.vsevolodganin.clicktrack.utils.media

import android.media.AudioFormat

object AudioFormatHelper {
    fun pcmEncodingToBitDepth(pcmEncoding: Int): Int {
        return when (pcmEncoding) {
            AudioFormat.ENCODING_PCM_8BIT -> 8
            AudioFormat.ENCODING_PCM_16BIT -> 16
            AudioFormat.ENCODING_PCM_24BIT_PACKED -> 24
            AudioFormat.ENCODING_PCM_32BIT,
            AudioFormat.ENCODING_PCM_FLOAT -> 32
            else -> throw IllegalArgumentException("Bad PCM encoding: $pcmEncoding")
        }
    }

    fun channelMaskToChannelCount(channelMask: Int): Int = Integer.bitCount(channelMask)

    fun bytesPerSample(bitDepth: Int): Int = bitDepth / 8

    fun bytesPerFrame(bytesPerSample: Int, channelCount: Int): Int = bytesPerSample * channelCount

    fun frameRate(sampleRate: Int, channelCount: Int): Int = sampleRate / channelCount
}
