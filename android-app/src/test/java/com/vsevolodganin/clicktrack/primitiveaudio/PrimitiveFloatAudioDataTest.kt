package com.vsevolodganin.clicktrack.primitiveaudio

import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import org.junit.Test
import org.junit.runner.RunWith
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

@RunWith(TestParameterInjector::class)
class PrimitiveFloatAudioDataTest {

    enum class Input(
        val resource: String,
        val encoding: PrimitiveAudioData.Encoding
    ) {
        U8("u8.pcm", PrimitiveAudioData.Encoding.PCM_UNSIGNED_8BIT),
        S16LE("s16le.pcm", PrimitiveAudioData.Encoding.PCM_SIGNED_16BIT_LITTLE_ENDIAN),
        S24LE("s24le.pcm", PrimitiveAudioData.Encoding.PCM_SIGNED_24BIT_LITTLE_ENDIAN),
        S32LE("s32le.pcm", PrimitiveAudioData.Encoding.PCM_SIGNED_32BIT_LITTLE_ENDIAN),
        F32LE("f32le.pcm", PrimitiveAudioData.Encoding.PCM_FLOAT_32BIT_LITTLE_ENDIAN)
    }

    @Test
    fun `given input PCM, when transcoding to float, then output is correctly encoded`(@TestParameter input: Input) {
        val actual = PrimitiveFloatAudioData.from(
            PrimitiveAudioData(
                bytes = ClassLoader.getSystemResourceAsStream("pcm/${input.resource}").use(InputStream::readBytes),
                encoding = input.encoding,
                sampleRate = SAMPLE_RATE,
                channelCount = CHANNEL_COUNT
            )
        )

        val expected = PrimitiveFloatAudioData(
            samples = ClassLoader.getSystemResourceAsStream("pcm/f32le.pcm").use(InputStream::readBytes).asFloatArray(),
            sampleRate = SAMPLE_RATE,
            channelCount = CHANNEL_COUNT
        )

        assertContentEquals(expected.samples, actual.samples)
        assertEquals(expected.sampleRate, actual.sampleRate)
        assertEquals(expected.channelCount, actual.channelCount)
    }

    private fun ByteArray.asFloatArray(): FloatArray {
        val buffer = ByteBuffer.wrap(this).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer()
        val array = FloatArray(buffer.limit())
        buffer.get(array)
        return array
    }

    private companion object {
        const val SAMPLE_RATE = 44100
        const val CHANNEL_COUNT = 1
    }
}
