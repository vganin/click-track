package com.vsevolodganin.clicktrack.audio

import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.AudioProcessor
import be.tarsos.dsp.BitDepthProcessor
import be.tarsos.dsp.MultichannelToMono
import be.tarsos.dsp.io.TarsosDSPAudioFormat
import be.tarsos.dsp.io.UniversalAudioInputStream
import be.tarsos.dsp.resample.RateTransposer
import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import me.tatarka.inject.annotations.Inject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.OutputStream

@ApplicationScope
@Inject
class PcmResampler {

    fun resample(
        inputData: ByteArray,
        inputBitDepth: Int,
        inputSampleRate: Int,
        inputChannelCount: Int,
        outputBitDepth: Int,
        outputSampleRate: Int,
    ): ByteArray {
        val inputAudioFormat = TarsosDSPAudioFormat(
            inputSampleRate.toFloat(),
            inputBitDepth,
            inputChannelCount,
            true,
            false
        )
        val inputStream = ByteArrayInputStream(inputData)
        val outputStream = ByteArrayOutputStream()

        val tarsosInputStream = UniversalAudioInputStream(inputStream, inputAudioFormat)

        val audioDispatcher = AudioDispatcher(tarsosInputStream, inputData.size, 0).apply {
            addAudioProcessor(MultichannelToMono(inputChannelCount, true))
            if (inputSampleRate != outputSampleRate) {
                addAudioProcessor(RateTransposer(outputSampleRate.toDouble() / inputSampleRate))
            }
            if (inputBitDepth != outputBitDepth) {
                addAudioProcessor(BitDepthProcessor().apply { bitDepth = outputBitDepth })
            }
            addAudioProcessor(OutputStreamProcessor(outputStream))
        }

        audioDispatcher.run()

        return outputStream.toByteArray()
    }

    fun mix(lhs: ByteArray, rhs: ByteArray): ByteArray {
        val (longer, shorter) = if (lhs.size > rhs.size) {
            lhs to rhs
        } else {
            rhs to lhs
        }
        val result = ByteArray(longer.size)
        for (index in shorter.indices) {
            result[index] = (longer[index] / 2 + shorter[index] / 2).toByte()
        }
        for (index in shorter.size..longer.lastIndex) {
            result[index] = (longer[index] / 2).toByte()
        }
        return result
    }

    private class OutputStreamProcessor(private val outputStream: OutputStream) : AudioProcessor {

        override fun process(audioEvent: AudioEvent): Boolean {
            outputStream.write(audioEvent.byteBuffer)
            return true
        }

        override fun processingFinished() = Unit
    }
}
