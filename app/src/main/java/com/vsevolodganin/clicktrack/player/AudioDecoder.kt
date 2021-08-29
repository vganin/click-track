package com.vsevolodganin.clicktrack.player

import android.content.res.AssetFileDescriptor
import android.media.AudioFormat
import android.media.AudioFormat.CHANNEL_OUT_5POINT1
import android.media.AudioFormat.CHANNEL_OUT_7POINT1_SURROUND
import android.media.AudioFormat.CHANNEL_OUT_DEFAULT
import android.media.AudioFormat.CHANNEL_OUT_FRONT_CENTER
import android.media.AudioFormat.CHANNEL_OUT_MONO
import android.media.AudioFormat.CHANNEL_OUT_QUAD
import android.media.AudioFormat.CHANNEL_OUT_STEREO
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
        val pcmEncoding: Int,
        val sampleRate: Int,
        val channelMask: Int,
        val bytes: ByteArray,
    )

    fun decodeAudioTrack(afd: AssetFileDescriptor, maxSeconds: Int): DecodingResult {
        val mediaExtractor = MediaExtractor()
        try {
            mediaExtractor.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            for (trackIndex in 0 until mediaExtractor.trackCount) {
                val trackFormat = mediaExtractor.getTrackFormat(trackIndex)
                val trackMime = requireNotNull(trackFormat.getString(MediaFormat.KEY_MIME))
                if (trackMime.startsWith("audio/")) {
                    return decodeAudioTrack(mediaExtractor, trackIndex, maxSeconds)
                }
            }
        } finally {
            mediaExtractor.release()
        }
        throw IllegalArgumentException("Failed to decode")
    }

    private fun decodeAudioTrack(mediaExtractor: MediaExtractor, trackIndex: Int, maxSeconds: Int): DecodingResult {
        val inputTrackFormat = mediaExtractor.getTrackFormat(trackIndex)
        val codecName = MediaCodecList(MediaCodecList.REGULAR_CODECS).findDecoderForFormat(inputTrackFormat)
        val codec = MediaCodec.createByCodecName(codecName)
        codec.configure(inputTrackFormat, null, null, 0)
        codec.start()

        var outputTrackFormat = codec.outputFormat

        val maxBytes = maxSeconds * outputTrackFormat.bytesPerSecond()
        val resultByteBuffer = ByteBuffer.allocateDirect(maxBytes)

        var sawInputEnd = false
        var sawOutputEnd = false
        mediaExtractor.selectTrack(trackIndex)
        while (!sawOutputEnd) {
            if (!sawInputEnd) {
                val inputBufferIndex = codec.dequeueInputBuffer(5000)
                if (inputBufferIndex >= 0) {
                    val inputBuffer = codec.getInputBuffer(inputBufferIndex)!!
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
                    outputTrackFormat = codec.outputFormat
                }
                outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER -> {
                    Timber.w("Received INFO_TRY_AGAIN_LATER")
                }
            }
        }

        codec.stop()
        codec.release()

        return DecodingResult(
            pcmEncoding = outputTrackFormat.pcmEncoding(),
            sampleRate = outputTrackFormat.sampleRate(),
            channelMask = outputTrackFormat.channelMask(),
            bytes = ByteArray(resultByteBuffer.position()).apply {
                resultByteBuffer.limit(resultByteBuffer.position())
                resultByteBuffer.position(0)
                resultByteBuffer.get(this)
            }
        )
    }
}

private fun MediaFormat.pcmEncoding(): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        getOptional(MediaFormat.KEY_PCM_ENCODING, MediaFormat::getInteger) ?: AudioFormat.ENCODING_PCM_16BIT
    } else {
        AudioFormat.ENCODING_PCM_16BIT
    }
}

private fun MediaFormat.channelCount(): Int {
    return getInteger(MediaFormat.KEY_CHANNEL_COUNT)
}

private fun MediaFormat.channelMask(): Int {
    return when (channelCount()) {
        1 -> CHANNEL_OUT_MONO
        2 -> CHANNEL_OUT_STEREO
        3 -> CHANNEL_OUT_STEREO or CHANNEL_OUT_FRONT_CENTER
        4 -> CHANNEL_OUT_QUAD
        5 -> CHANNEL_OUT_QUAD or CHANNEL_OUT_FRONT_CENTER
        6 -> CHANNEL_OUT_5POINT1
        8 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CHANNEL_OUT_7POINT1_SURROUND
        } else {
            CHANNEL_OUT_DEFAULT
        }
        else -> CHANNEL_OUT_DEFAULT
    }
}

private fun MediaFormat.sampleRate(): Int {
    return getInteger(MediaFormat.KEY_SAMPLE_RATE)
}

private fun MediaFormat.bytesPerSample(): Int {
    return when (val pcmEncoding = pcmEncoding()) {
        AudioFormat.ENCODING_PCM_8BIT -> 1
        AudioFormat.ENCODING_PCM_16BIT -> 2
        AudioFormat.ENCODING_PCM_24BIT_PACKED -> 3
        AudioFormat.ENCODING_PCM_32BIT,
        AudioFormat.ENCODING_PCM_FLOAT,
        -> 4
        AudioFormat.ENCODING_INVALID -> throw java.lang.IllegalArgumentException("Bad audio format $pcmEncoding")
        else -> throw java.lang.IllegalArgumentException("Bad audio format $pcmEncoding")
    }
}

private fun MediaFormat.bytesPerSecond(): Int {
    return sampleRate() * bytesPerSample() * channelCount()
}

private fun <T> MediaFormat.getOptional(key: String, getter: MediaFormat.(String) -> T): T? {
    return if (containsKey(key)) {
        getter.invoke(this, key)
    } else {
        null
    }
}
