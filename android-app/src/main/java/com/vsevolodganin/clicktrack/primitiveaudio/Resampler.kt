package com.vsevolodganin.clicktrack.primitiveaudio

class Resampler(
    private val channelCount: Int,
    private val inputRate: Int,
    private val outputRate: Int,
    quality: Quality
) {
    enum class Quality {
        Fastest,
        Low,
        Medium,
        High,
        Best,
    }

    private val nativePtr = createNative(
        channelCount,
        inputRate,
        outputRate,
        quality.ordinal,
    )

    fun resample(samples: FloatArray): FloatArray = resampleNative(
        nativePtr,
        samples,
        channelCount,
        inputRate,
        outputRate,
    )

    protected fun finalize() = destroyNative(nativePtr)

    private external fun createNative(
        channelCount: Int,
        inputRate: Int,
        outputRate: Int,
        quality: Int
    ): Long

    private external fun resampleNative(
        ptr: Long,
        samples: FloatArray,
        channelCount: Int,
        inputRate: Int,
        outputRate: Int,
    ): FloatArray

    private external fun destroyNative(ptr: Long)
}
