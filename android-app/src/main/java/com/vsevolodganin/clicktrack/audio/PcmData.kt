package com.vsevolodganin.clicktrack.audio

import com.vsevolodganin.clicktrack.utils.media.AudioFormatHelper

class PcmData(
    val bitDepth: Int,
    val sampleRate: Int,
    val channelCount: Int,
    val data: ByteArray,
)

val PcmData.bytesPerSample: Int get() = AudioFormatHelper.bytesPerSample(bitDepth)
val PcmData.bytesPerFrame: Int get() = AudioFormatHelper.bytesPerFrame(bytesPerSample, channelCount)
val PcmData.frameRate: Int get() = AudioFormatHelper.frameRate(sampleRate, channelCount)
