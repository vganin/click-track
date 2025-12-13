package com.vsevolodganin.clicktrack.primitiveaudio

import com.vsevolodganin.clicktrack.model.ClickSoundSource
import com.vsevolodganin.clicktrack.player.PlayerEvent
import com.vsevolodganin.clicktrack.soundlibrary.SoundSourceProvider
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.Inject

@Inject
class PrimitiveAudioMonoRenderer(
    @Assisted private val targetSampleRate: Int,
    private val primitiveAudioProvider: PrimitiveAudioProvider,
) {
    private val preparedSamples = mutableMapOf<ClickSoundSource, AudioData?>()

    fun renderToMonoSamples(events: Sequence<PlayerEvent>, soundSourceProvider: SoundSourceProvider): Sequence<Float> {
        return events
            .flatMap { playerEvent ->
                val audioData = playerEvent.soundType
                    ?.let(soundSourceProvider::provide)
                    ?.let(::getAudioData)
                    ?: AudioData(samples = FloatArray(0), channelCount = 1)

                val audioFrames = audioData.samples
                    .asSequence()
                    .chunked(audioData.channelCount)
                    .toList()

                val totalFramesNumber = convertDurationToFramesNumber(
                    duration = playerEvent.duration,
                    sampleRate = targetSampleRate,
                    channelCount = audioData.channelCount,
                )

                sequence {
                    audioFrames
                        .take(totalFramesNumber)
                        .forEach { frame ->
                            yield(frame.sumOf { sample -> sample.toDouble() / frame.size }.toFloat())
                        }

                    val restSilenceFramesNumber = maxOf(0, totalFramesNumber - audioFrames.size)
                    repeat(restSilenceFramesNumber) {
                        yield(0f)
                    }
                }
            }
    }

    private fun getAudioData(soundSource: ClickSoundSource): AudioData? {
        return preparedSamples.getOrPut(soundSource) {
            val audioData = primitiveAudioProvider.get(soundSource) ?: return@getOrPut null
            AudioData(
                samples = Resampler(
                    channelCount = audioData.channelCount,
                    inputRate = audioData.sampleRate,
                    outputRate = targetSampleRate,
                    quality = Resampler.Quality.Medium,
                ).resample(audioData.samples),
                channelCount = audioData.channelCount,
            )
        }
    }

    private class AudioData(
        val samples: FloatArray,
        val channelCount: Int,
    )
}
