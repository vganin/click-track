package com.vsevolodganin.clicktrack.export

import android.content.Context
import android.media.MediaCodec
import android.media.MediaCodecList
import android.media.MediaFormat
import android.media.MediaMuxer
import com.vsevolodganin.clicktrack.lib.ClickTrack
import com.vsevolodganin.clicktrack.player.toPlayerEvents
import com.vsevolodganin.clicktrack.sounds.SoundBank
import com.vsevolodganin.clicktrack.sounds.UserSelectedSounds
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundType
import com.vsevolodganin.clicktrack.sounds.model.Pcm16Data
import com.vsevolodganin.clicktrack.sounds.model.bytesPerFrame
import com.vsevolodganin.clicktrack.sounds.model.framesPerSecond
import com.vsevolodganin.clicktrack.utils.media.bytesPerSecond
import dagger.Reusable
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import kotlin.time.DurationUnit

@Reusable
class ExportToAudioFile @Inject constructor(
    private val context: Context,
    private val soundBank: SoundBank,
    private val userSelectedSounds: UserSelectedSounds,
) {
    suspend fun export(clickTrack: ClickTrack, onProgress: (Float) -> Unit, onFinished: () -> Unit): File? {
        val selectedSounds = userSelectedSounds.get().value ?: return null
        val strongSound = selectedSounds.strongBeat?.let(soundBank::get) ?: return null
        val weakSound = selectedSounds.weakBeat?.let(soundBank::get) ?: return null

        // TODO: Should resample sounds to user desired sample rate and channel count
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

            outputFile = File.createTempFile("click_track_export", ".m4a", context.cacheDir)

            muxer = MediaMuxer(outputFile.path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

            val codecName = MediaCodecList(MediaCodecList.REGULAR_CODECS).findEncoderForFormat(outputFormat)!!
            codec = MediaCodec.createByCodecName(codecName).apply {
                configure(outputFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
                start()
            }

            val trackByteSequence = clickTrack.render(strongSound, weakSound)
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

            onFinished()
        }
    }

    private fun ClickTrack.render(strongSound: Pcm16Data, weakSound: Pcm16Data): Sequence<Byte> {
        val playerEvents = toPlayerEvents()
        return sequence {
            for (event in playerEvents) {
                // TODO: Should resample and mix sounds
                val sound = event.sounds.firstOrNull()
                    ?.let {
                        when (it) {
                            ClickSoundType.STRONG -> strongSound
                            ClickSoundType.WEAK -> weakSound
                        }
                    }
                    ?: continue

                val maxFramesCount = (event.duration.toDouble(DurationUnit.SECONDS) * sound.framesPerSecond).toInt()
                val framesOfSound = (sound.data.size / sound.bytesPerFrame).coerceAtMost(maxFramesCount)
                val framesOfSilence = maxFramesCount - framesOfSound

                yieldAll(sound.data.asSequence().take(framesOfSound * sound.bytesPerFrame))
                yieldAll(sequence { repeat(framesOfSilence * sound.bytesPerFrame) { yield(0) } })
            }
        }
    }
}
