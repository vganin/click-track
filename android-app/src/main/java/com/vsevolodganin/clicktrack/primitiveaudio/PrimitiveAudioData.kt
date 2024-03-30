package com.vsevolodganin.clicktrack.primitiveaudio

class PrimitiveAudioData(
    val bytes: ByteArray,
    val encoding: Encoding,
    val sampleRate: Int,
    val channelCount: Int
) {
    /** [nativeIndex] should be the same as `encoding` argument for native `PrimitiveAudio` constructor */
    enum class Encoding(val nativeIndex: Int) {

        /** Same as [android.media.AudioFormat.ENCODING_PCM_8BIT] */
        PCM_8BIT(0),

        /** Same as [android.media.AudioFormat.ENCODING_PCM_16BIT] */
        PCM_16BIT(1),

        /** Same as [android.media.AudioFormat.ENCODING_PCM_24BIT_PACKED] */
        PCM_24BIT_PACKED(2),

        /** Same as [android.media.AudioFormat.ENCODING_PCM_32BIT] */
        PCM_32BIT(3),

        /** Same as [android.media.AudioFormat.ENCODING_PCM_FLOAT] */
        PCM_FLOAT(4),
    }
}

val PrimitiveAudioData.bytesPerSample: Int
    get() = when (encoding) {
        PrimitiveAudioData.Encoding.PCM_8BIT -> 1
        PrimitiveAudioData.Encoding.PCM_16BIT -> 2
        PrimitiveAudioData.Encoding.PCM_24BIT_PACKED -> 3
        PrimitiveAudioData.Encoding.PCM_32BIT,
        PrimitiveAudioData.Encoding.PCM_FLOAT -> 4
    }
val PrimitiveAudioData.bytesPerFrame: Int get() = bytesPerSample * channelCount
val PrimitiveAudioData.framesPerSecond: Int get() = sampleRate / channelCount
