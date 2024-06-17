package com.vsevolodganin.clicktrack.export

import android.app.Application
import android.media.AudioFormat
import android.media.MediaCodec
import android.media.MediaCodecList
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Build
import com.vsevolodganin.clicktrack.model.ClickTrack
import com.vsevolodganin.clicktrack.player.toPlayerEvents
import com.vsevolodganin.clicktrack.primitiveaudio.PrimitiveAudioMonoRenderer
import com.vsevolodganin.clicktrack.primitiveaudio.convertDurationToSamplesNumber
import com.vsevolodganin.clicktrack.primitiveaudio.convertSamplesNumberToDuration
import com.vsevolodganin.clicktrack.soundlibrary.SoundSourceProvider
import com.vsevolodganin.clicktrack.soundlibrary.UserSelectedSounds
import com.vsevolodganin.clicktrack.utils.log.Logger
import com.vsevolodganin.clicktrack.utils.media.pcmEncoding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import java.io.File
import java.nio.ByteOrder

@Inject
class ExportToAudioFile(
    private val application: Application,
    primitiveAudioMonoRendererFactory: (targetSampleRate: Int) -> PrimitiveAudioMonoRenderer,
    private val userSelectedSounds: UserSelectedSounds,
    private val logger: Logger,
) {
    private val primitiveAudioMonoRenderer = primitiveAudioMonoRendererFactory(SAMPLE_RATE)

    suspend fun export(clickTrack: ClickTrack, reportProgress: suspend (Float) -> Unit): File? {
        val soundSourceProvider = SoundSourceProvider(userSelectedSounds.get())

        var muxer: MediaMuxer? = null
        var codec: MediaCodec? = null
        var outputFile: File? = null

        return try {
            var outputFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, SAMPLE_RATE, CHANNEL_COUNT)
                .apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        setInteger(MediaFormat.KEY_PCM_ENCODING, AudioFormat.ENCODING_PCM_FLOAT)
                    }
                    setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE)
                }

            outputFile = withContext(Dispatchers.IO) {
                File.createTempFile("click_track_export", ".m4a", application.cacheDir)
            }

            muxer = MediaMuxer(outputFile.path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

            val codecName = MediaCodecList(MediaCodecList.REGULAR_CODECS).findEncoderForFormat(outputFormat)!!
            codec = MediaCodec.createByCodecName(codecName).apply {
                configure(outputFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
                start()
                outputFormat = getOutputFormat()
            }

            val trackSampleSequence = primitiveAudioMonoRenderer.renderToMonoSamples(clickTrack.toPlayerEvents(), soundSourceProvider)
            val trackSampleIterator = trackSampleSequence.iterator()
            val samplesToWrite = convertDurationToSamplesNumber(clickTrack.durationInTime, SAMPLE_RATE)
            val bufferInfo = MediaCodec.BufferInfo()

            var samplesWritten = 0
            var endOfInput = false
            var endOfOutput = false

            val coroutineContext = currentCoroutineContext()

            while (coroutineContext.isActive && (!endOfInput || !endOfOutput)) {
                if (!endOfInput) {
                    val inputBufferIndex = codec.dequeueInputBuffer(0L)
                    if (inputBufferIndex >= 0) {
                        val inputBuffer = codec.getInputBuffer(inputBufferIndex)!!
                        val presentationTimeUs = convertSamplesNumberToDuration(samplesWritten, SAMPLE_RATE).inWholeMicroseconds

                        val bytesWritten = when (outputFormat.pcmEncoding()) {
                            AudioFormat.ENCODING_PCM_FLOAT -> {
                                val sampleBuffer = inputBuffer.order(ByteOrder.nativeOrder()).asFloatBuffer()
                                while (trackSampleIterator.hasNext() && sampleBuffer.hasRemaining()) {
                                    sampleBuffer.put(trackSampleIterator.next())
                                }
                                samplesWritten += sampleBuffer.position()
                                sampleBuffer.position() * Float.SIZE_BYTES
                            }
                            AudioFormat.ENCODING_PCM_16BIT -> {
                                val sampleBuffer = inputBuffer.order(ByteOrder.nativeOrder()).asShortBuffer()
                                while (trackSampleIterator.hasNext() && sampleBuffer.hasRemaining()) {
                                    val nextFloatSample = trackSampleIterator.next()
                                    val nextShortSample = (nextFloatSample * Short.MAX_VALUE).toInt().toShort()
                                    sampleBuffer.put(nextShortSample)
                                }
                                samplesWritten += sampleBuffer.position()
                                sampleBuffer.position() * Short.SIZE_BYTES
                            }
                            else -> {
                                logger.logError(TAG, "Unsupported encoding")
                                return null
                            }
                        }

                        endOfInput = !trackSampleIterator.hasNext()

                        codec.queueInputBuffer(
                            inputBufferIndex,
                            0,
                            bytesWritten,
                            presentationTimeUs,
                            if (endOfInput) MediaCodec.BUFFER_FLAG_END_OF_STREAM else 0,
                        )

                        reportProgress(samplesWritten.toFloat() / samplesToWrite)
                    }
                }

                if (!endOfOutput) {
                    val outputBufferIndex = codec.dequeueOutputBuffer(bufferInfo, 0L)

                    if (outputBufferIndex >= 0) {
                        val outputBuffer = codec.getOutputBuffer(outputBufferIndex)!!
                        muxer.writeSampleData(0, outputBuffer, bufferInfo)
                        codec.releaseOutputBuffer(outputBufferIndex, false)
                    } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        // Not using `outputFormat` because of https://developer.android.com/reference/android/media/MediaCodec#CSD
                        muxer.addTrack(codec.outputFormat)
                        muxer.start()
                    }

                    endOfOutput = bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0
                }
            }

            if (coroutineContext.isActive) {
                outputFile
            } else {
                outputFile?.delete()
                null
            }
        } catch (t: Throwable) {
            logger.logError(TAG, "Failed to export track", t)
            outputFile?.delete()
            null
        } finally {
            try {
                codec?.stop()
            } catch (t: Throwable) {
                logger.logError(TAG, "Failed to stop codec", t)
            } finally {
                codec?.release()
            }

            try {
                muxer?.stop()
            } catch (t: Throwable) {
                logger.logError(TAG, "Failed to stop muxer", t)
            } finally {
                muxer?.release()
            }
        }
    }

    private companion object {
        const val TAG = "ExportToAudioFile"

        private const val SAMPLE_RATE = 44100
        private const val CHANNEL_COUNT = 1
        private const val BIT_RATE = 96 * 1024
    }
}
