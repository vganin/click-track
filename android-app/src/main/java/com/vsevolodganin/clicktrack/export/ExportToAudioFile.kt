package com.vsevolodganin.clicktrack.export

import android.app.Application
import android.media.MediaCodec
import android.media.MediaCodecList
import android.media.MediaFormat
import android.media.MediaMuxer
import com.vsevolodganin.clicktrack.audio.SoundBank
import com.vsevolodganin.clicktrack.audio.SoundSourceProvider
import com.vsevolodganin.clicktrack.audio.UserSelectedSounds
import com.vsevolodganin.clicktrack.audio.bytesPerFrame
import com.vsevolodganin.clicktrack.audio.framesPerSecond
import com.vsevolodganin.clicktrack.model.ClickTrack
import com.vsevolodganin.clicktrack.player.toPlayerEvents
import com.vsevolodganin.clicktrack.utils.media.bytesPerSecond
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import timber.log.Timber
import java.io.File
import kotlin.time.DurationUnit

@Inject
class ExportToAudioFile(
    private val application: Application,
    private val soundBank: SoundBank,
    private val userSelectedSounds: UserSelectedSounds,
) {
    suspend fun export(clickTrack: ClickTrack, onProgress: suspend (Float) -> Unit): File? {
        val soundSourceProvider = SoundSourceProvider(userSelectedSounds.get())

        // TODO: Should resample sounds to user desired sample rate and channel count
        val strongSound = userSelectedSounds.get().value?.strongBeat?.let(soundBank::get) ?: return null
        val targetSampleRate = strongSound.sampleRate
        val targetChannelCount = strongSound.channelCount

        var muxer: MediaMuxer? = null
        var codec: MediaCodec? = null
        var outputFile: File? = null

        return try {
            val outputFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, targetSampleRate, targetChannelCount)
                .apply {
                    setInteger(MediaFormat.KEY_BIT_RATE, 96 * 1024)
                }

            outputFile = withContext(Dispatchers.IO) {
                File.createTempFile("click_track_export", ".m4a", application.cacheDir)
            }

            muxer = MediaMuxer(outputFile.path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

            val codecName = MediaCodecList(MediaCodecList.REGULAR_CODECS).findEncoderForFormat(outputFormat)!!
            codec = MediaCodec.createByCodecName(codecName).apply {
                configure(outputFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
                start()
            }

            val trackByteSequence = clickTrack.render(soundSourceProvider)
            val trackByteIterator = trackByteSequence.iterator()
            val bytesToWrite = trackByteSequence.count()
            val bufferInfo = MediaCodec.BufferInfo()

            var bytesWritten = 0L
            var endOfInput = false
            var endOfOutput = false

            val coroutineContext = currentCoroutineContext()

            while (coroutineContext.isActive && (!endOfInput || !endOfOutput)) {
                if (!endOfInput) {
                    val inputBufferIndex = codec.dequeueInputBuffer(0L)
                    if (inputBufferIndex >= 0) {
                        val inputBuffer = codec.getInputBuffer(inputBufferIndex)!!
                        val presentationTimeUs = (bytesWritten.toDouble() / outputFormat.bytesPerSecond() * 1_000_000L).toLong()

                        while (trackByteIterator.hasNext() && inputBuffer.hasRemaining()) {
                            inputBuffer.put(trackByteIterator.next())
                            ++bytesWritten
                        }

                        onProgress(bytesWritten.toFloat() / bytesToWrite)

                        endOfInput = !trackByteIterator.hasNext()

                        codec.queueInputBuffer(
                            inputBufferIndex,
                            0,
                            inputBuffer.position(),
                            presentationTimeUs,
                            if (endOfInput) MediaCodec.BUFFER_FLAG_END_OF_STREAM else 0
                        )
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
            Timber.e(t, "Failed to export track")
            outputFile?.delete()
            null
        } finally {
            try {
                codec?.stop()
            } catch (t: Throwable) {
                Timber.e(t, "Failed to stop code")
            } finally {
                codec?.release()
            }

            try {
                muxer?.stop()
            } catch (t: Throwable) {
                Timber.e(t, "Failed to stop muxer")
            } finally {
                muxer?.release()
            }
        }
    }

    private fun ClickTrack.render(soundSourceProvider: SoundSourceProvider): Sequence<Byte> {
        val playerEvents = toPlayerEvents()
        return sequence {
            for (event in playerEvents) {
                // TODO: Should resample?
                val soundData = event.soundType
                    ?.let(soundSourceProvider::provide)
                    ?.let(soundBank::get)
                    ?: continue

                val maxFramesCount = (event.duration.toDouble(DurationUnit.SECONDS) * soundData.framesPerSecond).toInt()
                val framesOfSound = (soundData.data.size / soundData.bytesPerFrame).coerceAtMost(maxFramesCount)
                val framesOfSilence = maxFramesCount - framesOfSound

                yieldAll(soundData.data.asSequence().take(framesOfSound * soundData.bytesPerFrame))
                yieldAll(sequence { repeat(framesOfSilence * soundData.bytesPerFrame) { yield(0) } })
            }
        }
    }
}
