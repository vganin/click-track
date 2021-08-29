package com.vsevolodganin.clicktrack.player

import android.content.res.AssetFileDescriptor
import android.media.AudioFormat
import android.media.MediaCodec
import android.media.MediaCodecList
import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.Build
import java.nio.ByteBuffer
import javax.inject.Inject
import kotlin.math.min
import timber.log.Timber

// Implementation is loosely based on https://android.googlesource.com/platform/frameworks/base/+/master/media/jni/soundpool/Sound.cpp#48
class AudioDecoder @Inject constructor() {

    class DecodingResult(
        val audioFormat: Int,
        val sampleRate: Int,
        val channelMask: Int,
        val bytes: ByteArray,
    )

    fun decodeAudioTrack(afd: AssetFileDescriptor, maxBytesCount: Int): DecodingResult {
        val mediaExtractor = MediaExtractor()
        try {
            mediaExtractor.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            for (trackIndex in 0 until mediaExtractor.trackCount) {
                val trackFormat = mediaExtractor.getTrackFormat(trackIndex)
                val trackMime = requireNotNull(trackFormat.getString(MediaFormat.KEY_MIME))
                if (trackMime.startsWith("audio/")) {
                    return decodeAudioTrack(mediaExtractor, trackIndex, maxBytesCount)
                }
            }
        } finally {
            mediaExtractor.release()
        }
        throw IllegalArgumentException("Failed to decode")
    }

    private fun decodeAudioTrack(mediaExtractor: MediaExtractor, trackIndex: Int, maxBytesCount: Int): DecodingResult {
        var trackFormat = mediaExtractor.getTrackFormat(trackIndex)
        val channelCount = trackFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
        val frameSize = 2 * channelCount // PCM 16 BIT multiplied on channel count
        val resultByteBuffer = ByteBuffer.allocateDirect(maxBytesCount.sizeMultipleOf(frameSize))
        val codecName = MediaCodecList(MediaCodecList.REGULAR_CODECS).findDecoderForFormat(trackFormat)
        val codec = MediaCodec.createByCodecName(codecName)
        codec.configure(trackFormat, null, null, 0)
        codec.start()
        mediaExtractor.selectTrack(trackIndex)
        var sawInputEnd = false
        var sawOutputEnd = false
        trackFormat = codec.outputFormat
        while (!sawOutputEnd) {
            if (!sawInputEnd) {
                val inputBufferIndex = codec.dequeueInputBuffer(5000)
                if (inputBufferIndex >= 0) {
                    val inputBuffer = requireNotNull(codec.getInputBuffer(inputBufferIndex))
                    var sampleSize = mediaExtractor.readSampleData(inputBuffer, 0)
                    if (sampleSize < 0) {
                        sampleSize = 0
                        sawInputEnd = true
                    }
                    val presentationTimeUs = mediaExtractor.sampleTime
                    codec.queueInputBuffer(inputBufferIndex, 0, sampleSize, presentationTimeUs,
                        if (sawInputEnd) MediaCodec.BUFFER_FLAG_END_OF_STREAM else 0)
                    mediaExtractor.advance()
                }
            }
            val outputBufferInfo = MediaCodec.BufferInfo()
            val outputBufferIndex = codec.dequeueOutputBuffer(outputBufferInfo, 1)
            when {
                outputBufferIndex >= 0 -> {
                    if (outputBufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0 ||
                        resultByteBuffer.remaining() == 0
                    ) {
                        sawOutputEnd = true
                    }
                    val sizeToWrite = min(resultByteBuffer.remaining(), outputBufferInfo.size)
                    val outputBuffer = requireNotNull(codec.getOutputBuffer(outputBufferIndex))
                    resultByteBuffer.put(outputBuffer.duplicate().apply {
                        position(outputBufferInfo.offset)
                        limit(outputBufferInfo.offset + sizeToWrite)
                    })
                    codec.releaseOutputBuffer(outputBufferIndex, false)
                }
                outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                    trackFormat = codec.outputFormat
                }
                outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER -> {
                    Timber.w("Received INFO_TRY_AGAIN_LATER")
                }
            }
        }

        codec.stop()
        codec.release()

        return DecodingResult(
            audioFormat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                trackFormat.nullableGet(MediaFormat.KEY_PCM_ENCODING, MediaFormat::getInteger) ?: AudioFormat.ENCODING_PCM_16BIT
            } else AudioFormat.ENCODING_PCM_16BIT,
            sampleRate = trackFormat.nullableGet(MediaFormat.KEY_SAMPLE_RATE, MediaFormat::getInteger)
                ?: 44100,
            channelMask = trackFormat.nullableGet(MediaFormat.KEY_CHANNEL_MASK, MediaFormat::getInteger)
                .takeIf { it != 0 } ?: AudioFormat.CHANNEL_OUT_DEFAULT,
            bytes = ByteArray(resultByteBuffer.position()).apply {
                resultByteBuffer.limit(resultByteBuffer.position())
                resultByteBuffer.position(0)
                resultByteBuffer.get(this)
            }
        )
    }
}

private fun <T> MediaFormat.nullableGet(key: String, getter: MediaFormat.(String) -> T): T? {
    return if (containsKey(key)) {
        getter.invoke(this, key)
    } else {
        null
    }
}

private fun Int.sizeMultipleOf(multiple: Int): Int {
    var result = this
    while (result % multiple != 0) ++result
    return result
}
