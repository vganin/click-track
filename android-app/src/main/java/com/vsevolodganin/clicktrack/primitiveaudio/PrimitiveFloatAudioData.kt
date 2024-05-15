package com.vsevolodganin.clicktrack.primitiveaudio

class PrimitiveFloatAudioData(
    val samples: FloatArray,
    val sampleRate: Int,
    val channelCount: Int
) {
    companion object {
        fun from(audioData: PrimitiveAudioData): PrimitiveFloatAudioData = with(audioData) {
            val samples: FloatArray = when (encoding) {
                PrimitiveAudioData.Encoding.PCM_UNSIGNED_8BIT -> {
                    FloatArray(samplesNumber) { index ->
                        (bytes[index].toUByte().toInt() - 0x80).toFloat() / 0x80
                    }
                }

                PrimitiveAudioData.Encoding.PCM_SIGNED_16BIT_LITTLE_ENDIAN -> {
                    FloatArray(samplesNumber) { index ->
                        val byteIndex = index * 2
                        val value = (bytes[byteIndex].toInt() and 0xff shl 16) or
                            (bytes[byteIndex + 1].toInt() and 0xff shl 24)
                        value.toFloat() / 0x80000000
                    }
                }

                PrimitiveAudioData.Encoding.PCM_SIGNED_24BIT_LITTLE_ENDIAN -> {
                    FloatArray(samplesNumber) { index ->
                        val byteIndex = index * 3
                        val value = (bytes[byteIndex].toInt() and 0xff shl 8) or
                            (bytes[byteIndex + 1].toInt() and 0xff shl 16) or
                            (bytes[byteIndex + 2].toInt() and 0xff shl 24)
                        value.toFloat() / 0x80000000
                    }
                }

                PrimitiveAudioData.Encoding.PCM_SIGNED_32BIT_LITTLE_ENDIAN -> {
                    FloatArray(samplesNumber) { index ->
                        val byteIndex = index * 4
                        val value = (bytes[byteIndex].toInt() and 0xff) or
                            (bytes[byteIndex + 1].toInt() and 0xff shl 8) or
                            (bytes[byteIndex + 2].toInt() and 0xff shl 16) or
                            (bytes[byteIndex + 3].toInt() and 0xff shl 24)
                        value.toFloat() / 0x80000000
                    }
                }

                PrimitiveAudioData.Encoding.PCM_FLOAT_32BIT_LITTLE_ENDIAN -> {
                    FloatArray(samplesNumber) { index ->
                        val byteIndex = index * 4
                        val value = (bytes[byteIndex].toInt() and 0xff) or
                            (bytes[byteIndex + 1].toInt() and 0xff shl 8) or
                            (bytes[byteIndex + 2].toInt() and 0xff shl 16) or
                            (bytes[byteIndex + 3].toInt() and 0xff shl 24)
                        Float.fromBits(value)
                    }
                }
            }

            return PrimitiveFloatAudioData(
                samples = samples,
                sampleRate = sampleRate,
                channelCount = channelCount
            )
        }
    }
}
