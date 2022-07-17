package com.vsevolodganin.clicktrack.player

import com.vsevolodganin.clicktrack.model.AbstractPolyrhythm
import com.vsevolodganin.clicktrack.model.ClickTrack
import com.vsevolodganin.clicktrack.model.Cue
import com.vsevolodganin.clicktrack.model.NoteEvent
import com.vsevolodganin.clicktrack.model.TwoLayerPolyrhythm
import com.vsevolodganin.clicktrack.model.interval
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundType
import com.vsevolodganin.clicktrack.utils.collection.toRoundRobin
import com.vsevolodganin.clicktrack.utils.math.Rational
import com.vsevolodganin.clicktrack.utils.math.ZERO
import com.vsevolodganin.clicktrack.utils.math.compareTo
import com.vsevolodganin.clicktrack.utils.math.over
import com.vsevolodganin.clicktrack.utils.math.times
import com.vsevolodganin.clicktrack.utils.math.toRational
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class PlayerEvent(
    val duration: Duration,
    val soundTypes: List<ClickSoundType>
) {
    fun copy(
        duration: Duration = this.duration,
        soundTypes: List<ClickSoundType> = this.soundTypes
    ) = PlayerEvent(
        duration = duration,
        soundTypes = soundTypes
    )
}

fun ClickTrack.toPlayerEvents(): Sequence<PlayerEvent> {
    return cues.asSequence().flatMap(Cue::toPlayerEvents)
}

fun Cue.toPlayerEvents(): Sequence<PlayerEvent> {
    val tempo = bpm
    val polyrhythm = AbstractPolyrhythm(
        pattern1 = listOf(NoteEvent(timeSignature.noteCount.toRational(), NoteEvent.Type.NOTE)),
        pattern2 = pattern.events,
    )
    val bpmInterval = tempo.interval

    return sequence {
        if (polyrhythm.untilFirst > Rational.ZERO) {
            yield(delayEvent(bpmInterval * polyrhythm.untilFirst))
        }

        for (column in polyrhythm.columns) {
            val soundType = when {
                column.indices.contains(0) -> ClickSoundType.STRONG
                else -> ClickSoundType.WEAK
            }

            yield(
                singleSoundEvent(
                    duration = bpmInterval * column.untilNext,
                    soundType = soundType,
                )
            )
        }
    }.withDuration(durationAsTime)
}

fun TwoLayerPolyrhythm.toPlayerEvents(): Sequence<PlayerEvent> {
    val tempo = bpm
    val layer1NoteLength = 1.toRational()
    val layer2NoteLength = layer1 over layer2
    val polyrhythm = AbstractPolyrhythm(
        pattern1 = List(layer1) { NoteEvent(layer1NoteLength, NoteEvent.Type.NOTE) },
        pattern2 = List(layer2) { NoteEvent(layer2NoteLength, NoteEvent.Type.NOTE) }
    )

    return sequence {
        val bpmInterval = tempo.interval

        if (polyrhythm.untilFirst > Rational.ZERO) {
            yield(delayEvent(bpmInterval * polyrhythm.untilFirst))
        }

        for (column in polyrhythm.columns) {
            val soundTypes = column.indices.map { index ->
                when (index) {
                    0 -> ClickSoundType.STRONG
                    else -> ClickSoundType.WEAK
                }
            }

            yield(
                multipleSoundsEvent(
                    duration = bpmInterval * column.untilNext,
                    soundTypes = soundTypes,
                )
            )
        }
    }
}

private fun Sequence<PlayerEvent>.withDuration(duration: Duration): Sequence<PlayerEvent> {
    return sequence {
        val soundEventsCycledIterator = toRoundRobin().iterator()

        var runningDuration = Duration.ZERO
        while (true) {
            val next = soundEventsCycledIterator.next()
            runningDuration += next.duration

            if (runningDuration + Const.CLICK_MIN_DELTA >= duration) {
                if (runningDuration != duration) {
                    yield(next.copy(duration = next.duration - (runningDuration - duration)))
                } else {
                    yield(next)
                }
                return@sequence
            } else {
                yield(next)
            }
        }
    }
}

private fun singleSoundEvent(duration: Duration, soundType: ClickSoundType) = PlayerEvent(duration, listOf(soundType))
private fun multipleSoundsEvent(duration: Duration, soundTypes: List<ClickSoundType>) = PlayerEvent(duration, soundTypes)
private fun delayEvent(duration: Duration) = PlayerEvent(duration, emptyList())

private object Const {
    val CLICK_MIN_DELTA = 1.milliseconds
}
