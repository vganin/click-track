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
import dagger.Reusable
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import timber.log.Timber
import java.io.File
import java.nio.ByteBuffer
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

            val trackRawData = clickTrack.render(strongSound, weakSound)
            val trackBuffer = ByteBuffer.wrap(trackRawData)
            val bytesToWrite = trackRawData.size
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

                        while (trackBuffer.hasRemaining() && inputBuffer.hasRemaining()) {
                            inputBuffer.put(trackBuffer.get())
                            ++bytesWritten
                        }

                        onProgress(bytesWritten.toFloat() / bytesToWrite)

                        endOfInput = !trackBuffer.hasRemaining()

                        codec.queueInputBuffer(
                            inputBufferIndex,
                            0,
                            inputBuffer.position(),
                            0L,
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

    private fun ClickTrack.render(strongSound: Pcm16Data, weakSound: Pcm16Data): ByteArray {
        val result = mutableListOf<Byte>()

        for (event in toPlayerEvents()) {
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

            result += sound.data.slice(0 until framesOfSound * sound.bytesPerFrame)
            result += ByteArray(framesOfSilence * sound.bytesPerFrame).asIterable()
        }

        return result.toByteArray()
    }
}
