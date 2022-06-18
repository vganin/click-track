package com.vsevolodganin.clicktrack.sounds.model

class Pcm16Data(
    val sampleRate: Int,
    val channelCount: Int,
    val data: ByteArray,
)

val Pcm16Data.bytesPerSample: Int get() = 2
val Pcm16Data.bytesPerFrame: Int get() = bytesPerSample * channelCount
val Pcm16Data.framesPerSecond: Int get() = sampleRate / channelCount
