package com.vsevolodganin.clicktrack.player

import com.vsevolodganin.clicktrack.audio.SoundSourceProvider
import com.vsevolodganin.clicktrack.utils.collection.toRoundRobin
import kotlin.time.Duration

class PlayerAction(
    val interval: Duration,
    val action: suspend () -> Unit,
)

fun Sequence<PlayerEvent>.toActions(
    soundSourceProvider: SoundSourceProvider,
    reportLatency: (Duration) -> Unit,
    soundPool: PlayerSoundPool,
) = map {
    it.toAction(
        soundSourceProvider = soundSourceProvider,
        reportLatency = reportLatency,
        soundPool = soundPool,
    )
}

fun PlayerEvent.toAction(
    soundSourceProvider: SoundSourceProvider,
    reportLatency: (Duration) -> Unit,
    soundPool: PlayerSoundPool,
) = PlayerAction(
    interval = duration,
    action = {
        if (soundType != null) {
            val soundSource = soundSourceProvider.provide(soundType)
            soundSource?.let(soundPool::play)
            reportLatency(soundSource?.let(soundPool::latency) ?: Duration.ZERO)
        }
    }
)

fun Sequence<PlayerAction>.withSideEffect(
    atIndex: Int,
    action: () -> Unit
) = mapIndexed { index, event ->
    if (index == atIndex) {
        PlayerAction(
            interval = event.interval,
            action = {
                event.action()
                action()
            }
        )
    } else {
        event
    }
}

fun Sequence<PlayerAction>.startingAt(startAt: Duration): Sequence<PlayerAction> {
    var runningDuration = Duration.ZERO
    var dropCount = 0
    for (event in this) {
        if (runningDuration >= startAt) {
            break
        }
        dropCount += 1
        runningDuration += event.interval
    }

    return sequence {
        yield(
            PlayerAction(
                interval = (runningDuration - startAt).coerceAtLeast(Duration.ZERO),
                action = {},
            )
        )
        yieldAll(drop(dropCount))
    }
}

fun Sequence<PlayerAction>.loop(loop: Boolean): Sequence<PlayerAction> {
    return if (loop) toRoundRobin() else this
}
