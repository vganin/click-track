package com.vsevolodganin.clicktrack.training

import com.vsevolodganin.clicktrack.model.BeatsPerMinute
import com.vsevolodganin.clicktrack.model.ClickTrack
import com.vsevolodganin.clicktrack.model.Cue
import com.vsevolodganin.clicktrack.model.CueDuration
import com.vsevolodganin.clicktrack.model.TimeSignature
import com.vsevolodganin.clicktrack.model.asTimeGiven
import com.vsevolodganin.clicktrack.training.TrainingEditState.TrainingMode
import me.tatarka.inject.annotations.Inject
import kotlin.time.Duration

@Inject
class TrainingClickTrackGenerator() {
    fun generate(trainingState: TrainingValidState, name: String): ClickTrack {
        return ClickTrack(
            name = name,
            cues = when (trainingState.mode) {
                TrainingMode.INCREASE_TEMPO -> when (val ending = trainingState.ending) {
                    is TrainingValidState.Ending.ByTempo -> generateIncreasingTempoCuesWithTempoEnding(
                        startingTempo = trainingState.startingTempo,
                        tempoIncrement = trainingState.tempoChange,
                        endingTempo = ending.endingTempo,
                        segmentLength = trainingState.segmentLength,
                    )
                    is TrainingValidState.Ending.ByTime -> generateIncreasingTempoCuesWithTimeEnding(
                        startingTempo = trainingState.startingTempo,
                        tempoIncrement = trainingState.tempoChange,
                        endingTime = ending.duration,
                        segmentLength = trainingState.segmentLength,
                    )
                }
                TrainingMode.DECREASE_TEMPO -> when (val ending = trainingState.ending) {
                    is TrainingValidState.Ending.ByTempo -> generateDecreasingTempoCuesWithTempoEnding(
                        startingTempo = trainingState.startingTempo,
                        tempoDecrement = trainingState.tempoChange,
                        endingTempo = ending.endingTempo,
                        segmentLength = trainingState.segmentLength,
                    )
                    is TrainingValidState.Ending.ByTime -> generateDecreasingTempoCuesWithTimeEnding(
                        startingTempo = trainingState.startingTempo,
                        tempoDecrement = trainingState.tempoChange,
                        endingTime = ending.duration,
                        segmentLength = trainingState.segmentLength,
                    )
                }
            },
            loop = false,
        )
    }

    private fun generateIncreasingTempoCuesWithTempoEnding(
        startingTempo: BeatsPerMinute,
        tempoIncrement: BeatsPerMinute,
        endingTempo: BeatsPerMinute,
        segmentLength: CueDuration,
    ): List<Cue> {
        if (startingTempo > endingTempo) return emptyList()

        return buildList {
            var runningTempo = startingTempo

            while (runningTempo < endingTempo) {
                this += Cue(
                    bpm = runningTempo,
                    timeSignature = DefaultTimeSignature,
                    duration = segmentLength,
                )

                runningTempo += tempoIncrement
            }

            this += Cue(
                bpm = endingTempo,
                timeSignature = DefaultTimeSignature,
                duration = segmentLength,
            )
        }
    }

    private fun generateDecreasingTempoCuesWithTempoEnding(
        startingTempo: BeatsPerMinute,
        tempoDecrement: BeatsPerMinute,
        endingTempo: BeatsPerMinute,
        segmentLength: CueDuration,
    ): List<Cue> {
        if (startingTempo < endingTempo) return emptyList()

        return buildList {
            var runningTempo = startingTempo

            while (runningTempo > endingTempo) {
                this += Cue(
                    bpm = runningTempo,
                    timeSignature = DefaultTimeSignature,
                    duration = segmentLength,
                )

                if (runningTempo > tempoDecrement) {
                    runningTempo -= tempoDecrement
                } else {
                    break
                }
            }

            this += Cue(
                bpm = endingTempo,
                timeSignature = DefaultTimeSignature,
                duration = segmentLength,
            )
        }
    }

    private fun generateIncreasingTempoCuesWithTimeEnding(
        startingTempo: BeatsPerMinute,
        tempoIncrement: BeatsPerMinute,
        endingTime: Duration,
        segmentLength: CueDuration,
    ): List<Cue> {
        return buildList {
            var runningDuration = Duration.ZERO
            var runningTempo = startingTempo

            while (runningDuration < endingTime) {
                if (runningTempo < BeatsPerMinute.MAX) {
                    val segmentLengthAsTime = segmentLength.asTimeGiven(runningTempo, DefaultTimeSignature)
                    val segmentLengthCoerced = if (runningDuration + segmentLengthAsTime <= endingTime) {
                        segmentLength
                    } else {
                        CueDuration.Time(endingTime - runningDuration)
                    }
                    this += Cue(
                        bpm = runningTempo,
                        timeSignature = DefaultTimeSignature,
                        duration = segmentLengthCoerced,
                    )
                    runningDuration += segmentLengthAsTime
                    runningTempo += tempoIncrement
                } else {
                    this += Cue(
                        bpm = runningTempo,
                        timeSignature = DefaultTimeSignature,
                        duration = CueDuration.Time(endingTime - runningDuration),
                    )
                    runningDuration = endingTime
                }
            }
        }
    }

    private fun generateDecreasingTempoCuesWithTimeEnding(
        startingTempo: BeatsPerMinute,
        tempoDecrement: BeatsPerMinute,
        endingTime: Duration,
        segmentLength: CueDuration,
    ): List<Cue> {
        return buildList {
            var runningDuration = Duration.ZERO
            var runningTempo = startingTempo

            while (runningDuration < endingTime) {
                if (runningTempo > tempoDecrement) {
                    val segmentLengthAsTime = segmentLength.asTimeGiven(runningTempo, DefaultTimeSignature)
                    val segmentLengthCoerced = if (runningDuration + segmentLengthAsTime <= endingTime) {
                        segmentLength
                    } else {
                        CueDuration.Time(endingTime - runningDuration)
                    }
                    this += Cue(
                        bpm = runningTempo,
                        timeSignature = DefaultTimeSignature,
                        duration = segmentLengthCoerced,
                    )
                    runningDuration += segmentLengthAsTime
                    runningTempo -= tempoDecrement
                } else {
                    this += Cue(
                        bpm = runningTempo,
                        timeSignature = DefaultTimeSignature,
                        duration = CueDuration.Time(endingTime - runningDuration),
                    )
                    runningDuration = endingTime
                }
            }
        }
    }
}

private val DefaultTimeSignature = TimeSignature(4, 4)
