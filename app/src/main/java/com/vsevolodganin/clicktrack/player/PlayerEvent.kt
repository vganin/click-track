package com.vsevolodganin.clicktrack.player

import com.vsevolodganin.clicktrack.lib.AbstractPolyrhythm
import com.vsevolodganin.clicktrack.lib.ClickTrack
import com.vsevolodganin.clicktrack.lib.Cue
import com.vsevolodganin.clicktrack.lib.NoteEvent
import com.vsevolodganin.clicktrack.lib.TwoLayerPolyrhythm
import com.vsevolodganin.clicktrack.lib.interval
import com.vsevolodganin.clicktrack.lib.math.Rational
import com.vsevolodganin.clicktrack.lib.math.ZERO
import com.vsevolodganin.clicktrack.lib.math.compareTo
import com.vsevolodganin.clicktrack.lib.math.over
import com.vsevolodganin.clicktrack.lib.math.times
import com.vsevolodganin.clicktrack.lib.math.toRational
import com.vsevolodganin.clicktrack.lib.utils.collection.toRoundRobin
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundType
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class PlayerEvent(
    val duration: Duration,
    val sounds: List<ClickSoundType>
) {
    fun copy(
        duration: Duration = this.duration,
        sounds: List<ClickSoundType> = this.sounds
    ) = PlayerEvent(
        duration = duration,
        sounds = sounds
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
            val sound = when {
                column.indices.contains(0) -> ClickSoundType.STRONG
                else -> ClickSoundType.WEAK
            }

            yield(
                singleSoundEvent(
                    duration = bpmInterval * column.untilNext,
                    sound = sound,
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
            val sounds = column.indices.map { index ->
                when (index) {
                    0 -> ClickSoundType.STRONG
                    else -> ClickSoundType.WEAK
                }
            }

            yield(
                multipleSoundsEvent(
                    duration = bpmInterval * column.untilNext,
                    sounds = sounds,
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

private fun singleSoundEvent(duration: Duration, sound: ClickSoundType) = PlayerEvent(duration, listOf(sound))
private fun multipleSoundsEvent(duration: Duration, sounds: List<ClickSoundType>) = PlayerEvent(duration, sounds)
private fun delayEvent(duration: Duration) = PlayerEvent(duration, emptyList())

private object Const {
    val CLICK_MIN_DELTA = 1.milliseconds
}
