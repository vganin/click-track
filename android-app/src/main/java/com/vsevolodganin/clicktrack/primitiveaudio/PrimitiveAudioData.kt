package com.vsevolodganin.clicktrack.primitiveaudio

import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

class PrimitiveAudioData(
    val bytes: ByteArray,
    val encoding: Encoding,
    val sampleRate: Int,
    val channelCount: Int,
) {
    enum class Encoding {
        PCM_UNSIGNED_8BIT,
        PCM_SIGNED_16BIT_LITTLE_ENDIAN,
        PCM_SIGNED_24BIT_LITTLE_ENDIAN,
        PCM_SIGNED_32BIT_LITTLE_ENDIAN,
        PCM_FLOAT_32BIT_LITTLE_ENDIAN,
    }
}

val PrimitiveAudioData.bytesPerSample: Int
    get() = when (encoding) {
        PrimitiveAudioData.Encoding.PCM_UNSIGNED_8BIT -> 1
        PrimitiveAudioData.Encoding.PCM_SIGNED_16BIT_LITTLE_ENDIAN -> 2
        PrimitiveAudioData.Encoding.PCM_SIGNED_24BIT_LITTLE_ENDIAN -> 3
        PrimitiveAudioData.Encoding.PCM_SIGNED_32BIT_LITTLE_ENDIAN,
        PrimitiveAudioData.Encoding.PCM_FLOAT_32BIT_LITTLE_ENDIAN,
        -> 4
    }

val PrimitiveAudioData.samplesNumber: Int get() = bytes.size / bytesPerSample

fun convertDurationToSamplesNumber(
    duration: Duration,
    sampleRate: Int,
): Int {
    return (duration.toDouble(DurationUnit.SECONDS) * sampleRate).roundToInt()
}

fun convertDurationToFramesNumber(
    duration: Duration,
    sampleRate: Int,
    channelCount: Int,
): Int {
    return (duration.toDouble(DurationUnit.SECONDS) / channelCount * sampleRate).roundToInt()
}

fun convertSamplesNumberToDuration(
    samplesNumber: Int,
    sampleRate: Int,
): Duration {
    return (samplesNumber.toDouble() / sampleRate).seconds
}

fun convertFramesNumberToDuration(
    framesNumber: Int,
    sampleRate: Int,
    channelCount: Int,
): Duration {
    return (framesNumber.toDouble() / sampleRate * channelCount).seconds
}
