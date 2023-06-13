package com.vsevolodganin.clicktrack.audio

class PcmData(
    val bitDepth: Int,
    val sampleRate: Int,
    val channelCount: Int,
    val data: ByteArray,
)

val PcmData.bytesPerSample: Int get() = bitDepth / 8
val PcmData.bytesPerFrame: Int get() = bytesPerSample * channelCount
val PcmData.frameRate: Int get() = sampleRate / channelCount
