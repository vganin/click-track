package com.vsevolodganin.clicktrack.player

import android.content.res.AssetFileDescriptor
import android.media.MediaCodec
import android.media.MediaCodecList
import android.media.MediaExtractor
import android.media.MediaFormat
import com.vsevolodganin.clicktrack.sounds.model.Pcm16Data
import com.vsevolodganin.clicktrack.utils.media.bytesPerSecond
import com.vsevolodganin.clicktrack.utils.media.channelCount
import com.vsevolodganin.clicktrack.utils.media.sampleRate
import timber.log.Timber
import java.nio.ByteBuffer
import javax.inject.Inject
import kotlin.math.min

class AudioDecoder @Inject constructor() {
    fun extractPcm(afd: AssetFileDescriptor, maxSeconds: Int): Pcm16Data? {
        val mediaExtractor = MediaExtractor()

        try {
            mediaExtractor.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            for (trackIndex in 0 until mediaExtractor.trackCount) {
                val trackFormat = mediaExtractor.getTrackFormat(trackIndex)
                val trackMime = trackFormat.getString(MediaFormat.KEY_MIME)!!
                if (trackMime.startsWith("audio/")) {
                    return extractPcm(mediaExtractor, trackIndex, trackFormat, maxSeconds)
                }
            }
        } catch (t: Throwable) {
            Timber.e(t, "Failed to extract PCM")
        } finally {
            mediaExtractor.release()
        }

        return null
    }

    private fun extractPcm(mediaExtractor: MediaExtractor, trackIndex: Int, trackFormat: MediaFormat, maxSeconds: Int): Pcm16Data? {
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
                            if (endOfInput) MediaCodec.BUFFER_FLAG_END_OF_STREAM else 0
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

            Pcm16Data(
                sampleRate = outputTrackFormat.sampleRate(),
                channelCount = outputTrackFormat.channelCount(),
                data = resultByteBuffer.array().copyOf(resultByteBuffer.position()),
            )
        } finally {
            try {
                codec?.stop()
            } finally {
                codec?.release()
            }
        }
    }
}
