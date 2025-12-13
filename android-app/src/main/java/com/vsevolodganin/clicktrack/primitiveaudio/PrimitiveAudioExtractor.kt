package com.vsevolodganin.clicktrack.primitiveaudio

import android.content.res.AssetFileDescriptor
import android.media.AudioFormat
import android.media.MediaCodec
import android.media.MediaCodecList
import android.media.MediaExtractor
import android.media.MediaFormat
import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import com.vsevolodganin.clicktrack.utils.log.Logger
import com.vsevolodganin.clicktrack.utils.media.bytesPerSecond
import com.vsevolodganin.clicktrack.utils.media.channelCount
import com.vsevolodganin.clicktrack.utils.media.pcmEncoding
import com.vsevolodganin.clicktrack.utils.media.sampleRate
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import java.nio.ByteBuffer
import kotlin.math.min

@SingleIn(ApplicationScope::class)
@Inject
class PrimitiveAudioExtractor(
    private val logger: Logger,
) {
    fun extract(afd: AssetFileDescriptor, maxSeconds: Int): PrimitiveAudioData? {
        val mediaExtractor = MediaExtractor()

        try {
            mediaExtractor.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            for (trackIndex in 0 until mediaExtractor.trackCount) {
                val trackFormat = mediaExtractor.getTrackFormat(trackIndex)
                val trackMime = trackFormat.getString(MediaFormat.KEY_MIME) ?: continue
                if (trackMime.startsWith("audio/")) {
                    return extractPcm(mediaExtractor, trackIndex, trackFormat, maxSeconds)
                }
            }
        } catch (t: Throwable) {
            logger.logError(TAG, "Failed to extract PCM", t)
        } finally {
            mediaExtractor.release()
        }

        return null
    }

    private fun extractPcm(
        mediaExtractor: MediaExtractor,
        trackIndex: Int,
        trackFormat: MediaFormat,
        maxSeconds: Int,
    ): PrimitiveAudioData? {
        var codec: MediaCodec? = null

        return try {
            val codecName = MediaCodecList(MediaCodecList.REGULAR_CODECS).findDecoderForFormat(trackFormat)
            codec = MediaCodec.createByCodecName(codecName).apply {
                configure(trackFormat, null, null, 0)
                start()
            }

            val outputTrackFormat = codec.outputFormat
            val maxBytes = maxSeconds * outputTrackFormat.bytesPerSecond()
            val resultByteBuffer = ByteBuffer.allocateDirect(maxBytes)
            var endOfInput = false
            var endOfOutput = false

            mediaExtractor.selectTrack(trackIndex)

            while (!endOfInput || !endOfOutput) {
                if (!endOfInput) {
                    val inputBufferIndex = codec.dequeueInputBuffer(0L)

                    if (inputBufferIndex >= 0) {
                        val inputBuffer = codec.getInputBuffer(inputBufferIndex)!!
                        var sampleSize = mediaExtractor.readSampleData(inputBuffer, 0)
                        if (sampleSize < 0) {
                            sampleSize = 0
                            endOfInput = true
                        }
                        codec.queueInputBuffer(
                            inputBufferIndex,
                            0,
                            sampleSize,
                            mediaExtractor.sampleTime,
                            if (endOfInput) MediaCodec.BUFFER_FLAG_END_OF_STREAM else 0,
                        )
                        mediaExtractor.advance()
                    }
                }

                if (!endOfOutput) {
                    val bufferInfo = MediaCodec.BufferInfo()
                    val outputBufferIndex = codec.dequeueOutputBuffer(bufferInfo, 0L)

                    if (outputBufferIndex >= 0) {
                        if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0 ||
                            resultByteBuffer.remaining() == 0
                        ) {
                            endOfInput = true
                            endOfOutput = true
                        }

                        val sizeToWrite = min(resultByteBuffer.remaining(), bufferInfo.size)
                        val outputBuffer = codec.getOutputBuffer(outputBufferIndex)!!

                        repeat(sizeToWrite) {
                            resultByteBuffer.put(outputBuffer.get())
                        }

                        codec.releaseOutputBuffer(outputBufferIndex, false)
                    }
                }
            }

            PrimitiveAudioData(
                bytes = resultByteBuffer.array().copyOf(resultByteBuffer.position()),
                encoding = audioFormatEncodingToCommon(outputTrackFormat.pcmEncoding()) ?: return null,
                sampleRate = outputTrackFormat.sampleRate(),
                channelCount = outputTrackFormat.channelCount(),
            )
        } finally {
            try {
                codec?.stop()
            } finally {
                codec?.release()
            }
        }
    }

    private fun audioFormatEncodingToCommon(encoding: Int): PrimitiveAudioData.Encoding? = when (encoding) {
        AudioFormat.ENCODING_PCM_8BIT -> PrimitiveAudioData.Encoding.PCM_UNSIGNED_8BIT
        AudioFormat.ENCODING_PCM_16BIT -> PrimitiveAudioData.Encoding.PCM_SIGNED_16BIT_LITTLE_ENDIAN
        AudioFormat.ENCODING_PCM_24BIT_PACKED -> PrimitiveAudioData.Encoding.PCM_SIGNED_24BIT_LITTLE_ENDIAN
        AudioFormat.ENCODING_PCM_32BIT -> PrimitiveAudioData.Encoding.PCM_SIGNED_32BIT_LITTLE_ENDIAN
        AudioFormat.ENCODING_PCM_FLOAT -> PrimitiveAudioData.Encoding.PCM_FLOAT_32BIT_LITTLE_ENDIAN
        else -> {
            logger.logError(TAG, "Unknown PCM encoding: $encoding")
            null
        }
    }

    private companion object {
        const val TAG = "PrimitiveAudioExtractor"
    }
}
