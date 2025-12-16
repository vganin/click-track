package com.vsevolodganin.clicktrack.utils.resampler

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cosh
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Oboe's [MultiChannelResampler](https://github.com/google/oboe/blob/8a0b08994c54bec3d1bbbe3c82be6d661fa26ea1/src/flowgraph/resampler/MultiChannelResampler.h#L41)
 * rewritten in Kotlin with AI.
 */
class MultiChannelResampler(
    val channelCount: Int,
    val inputRate: Int,
    val outputRate: Int,
    val numTaps: Int,
    val normalizedCutoff: Float = DEFAULT_NORMALIZED_CUTOFF,
) {
    constructor(
        channelCount: Int,
        inputRate: Int,
        outputRate: Int,
        quality: Quality,
        normalizedCutoff: Float = DEFAULT_NORMALIZED_CUTOFF,
    ) : this(
        channelCount = channelCount,
        inputRate = inputRate,
        outputRate = outputRate,
        numTaps = when (quality) {
            Quality.Fastest -> 2
            Quality.Low -> 4
            Quality.Medium -> 8
            Quality.High -> 16
            Quality.Best -> 32
        },
        normalizedCutoff = normalizedCutoff,
    )

    enum class Quality { Fastest, Low, Medium, High, Best }

    /**
     * Resample interleaved float PCM.
     * @param input Interleaved float samples with [channelCount] channels.
     * @return New interleaved buffer at [outputRate].
     */
    fun resample(input: FloatArray): FloatArray {
        if (input.isEmpty()) return FloatArray(0)
        val framesIn = input.size / channelCount
        if (framesIn == 0) return FloatArray(0)

        // Estimate output frames and allocate.
        val framesOut = ((framesIn.toLong() * outputRate) / inputRate).toInt()
        val output = FloatArray(framesOut * channelCount)

        // Downsampling requires low-pass filtering; upsampling can set cutoff=1.0.
        val cutoffScaler = if (outputRate < inputRate) normalizedCutoff * (outputRate.toFloat() / inputRate.toFloat()) else 1.0f

        val halfTaps = numTaps / 2
        val tapsRadius = halfTaps // symmetric window radius

        // For each output frame, compute source time in input sample domain.
        // Use double precision for accumulator to reduce drift.
        val rateRatio = inputRate.toDouble() / outputRate.toDouble()
        var outIndex = 0

        for (n in 0 until framesOut) {
            val t = n * rateRatio // fractional input frame position
            val tFloor = floor(t).toInt()

            // For each channel, accumulate windowed sinc
            for (ch in 0 until channelCount) {
                var num = 0.0
                var den = 0.0
                // Window from -tapsRadius+1 .. +tapsRadius inclusive to keep numTaps terms
                val start = tFloor - tapsRadius + 1
                val end = tFloor + tapsRadius
                for (i in start..end) {
                    val x = t - i // fractional distance
                    val w = sinc(piTimes(x) * cutoffScaler) * hyperbolicCosineWindow(normalizeWindowArg(x, tapsRadius), HC_ALPHA)
                    den += w
                    val idxFrame = i
                    if (idxFrame in 0 until framesIn) {
                        val sample = input[idxFrame * channelCount + ch]
                        num += w * sample
                    }
                }
                val value = if (abs(den) > 1e-12) (num / den).toFloat() else 0f
                output[outIndex + ch] = value
            }
            outIndex += channelCount
        }

        return output
    }

    private fun sinc(x: Double): Double {
        val ax = abs(x)
        return if (ax < 1.0e-9) 1.0 else sin(x) / x
    }

    private fun piTimes(x: Double): Double = PI * x

    // Hyperbolic-cosine window approximation; x in [-1, 1].
    private fun hyperbolicCosineWindow(x: Double, alpha: Double): Double {
        val x2 = x * x
        if (x2 >= 1.0) return 0.0
        val w = alpha * sqrt(1.0 - x2)
        val invCoshAlpha = 1.0 / cosh(alpha)
        return cosh(w) * invCoshAlpha
    }

    // Map distance to [-1, 1] across the taps radius, clamped.
    private fun normalizeWindowArg(distance: Double, radius: Int): Double {
        val x = distance / radius
        return when {
            x < -1.0 -> -1.0
            x > 1.0 -> 1.0
            else -> x
        }
    }

    companion object {
        const val DEFAULT_NORMALIZED_CUTOFF: Float = 0.70f

        // Empirical alpha producing ~60 dB attenuation used by the native approximation by default
        // via setStopBandAttenuation(60).
        private const val DEFAULT_ATTENUATION_DB: Double = 60.0
        private val HC_ALPHA: Double = run {
            // Port of setStopBandAttenuation(): alpha = (-325.1e-6*A + 0.1677)*A - 3.149
            @Suppress("ktlint:standard:property-naming")
            val A = DEFAULT_ATTENUATION_DB
            ((-325.1e-6 * A + 0.1677) * A) - 3.149
        }
    }
}
