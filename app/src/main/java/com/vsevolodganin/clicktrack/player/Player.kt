package com.vsevolodganin.clicktrack.player

import com.vsevolodganin.clicktrack.di.component.PlayerServiceScoped
import com.vsevolodganin.clicktrack.di.module.MainDispatcher
import com.vsevolodganin.clicktrack.di.module.PlayerDispatcher
import com.vsevolodganin.clicktrack.lib.ClickSoundSource
import com.vsevolodganin.clicktrack.lib.Cue
import com.vsevolodganin.clicktrack.lib.interval
import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import com.vsevolodganin.clicktrack.state.PlaybackState
import com.vsevolodganin.clicktrack.utils.coroutine.MutableNonConflatedStateFlow
import com.vsevolodganin.clicktrack.utils.coroutine.delay
import com.vsevolodganin.clicktrack.utils.coroutine.tick
import com.vsevolodganin.clicktrack.utils.time.rem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource
import kotlin.time.milliseconds

interface Player {
    suspend fun start(clickTrack: ClickTrackWithId, atProgress: Double? = null)
    suspend fun pause()
    suspend fun stop()

    fun playbackState(): Flow<PlaybackState?>
}

@PlayerServiceScoped
class PlayerImpl @Inject constructor(
    private val soundPool: PlayerSoundPool,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @PlayerDispatcher private val playerDispatcher: CoroutineDispatcher,
) : Player {

    private var playerJob: Job? = null
    private val playbackStateMutex = Mutex()
    private var playbackState = MutableNonConflatedStateFlow<InternalPlaybackState?>(null)
    private var pausedState = MutableNonConflatedStateFlow(false)

    override suspend fun start(clickTrack: ClickTrackWithId, atProgress: Double?): Unit = withContext(mainDispatcher) {
        val currentProgress = currentProgress()
        val atProgressCoerced = atProgress ?: currentProgress ?: 0.0
        val startAtTime = clickTrack.value.durationInTime * atProgressCoerced

        updatePlaybackState {
            InternalPlaybackState(
                clickTrack = clickTrack,
                progress = atProgressCoerced,
                startMark = TimeSource.Monotonic.markNow(),
            )
        }

        playerJob?.cancel()
        playerJob = launch(playerDispatcher) {
            pausedState.setValue(false)
            startImpl(clickTrack, startAtTime)
            stop()
        }
    }

    override suspend fun pause() {
        pausedState.setValue(true)
    }

    override suspend fun stop(): Unit = withContext(mainDispatcher) {
        playerJob?.cancel()
        playerJob = null
        updatePlaybackState { null }
    }

    override fun playbackState(): Flow<PlaybackState?> {
        return playbackState
            .map { internalPlaybackState ->
                internalPlaybackState ?: return@map null
                PlaybackState(
                    clickTrack = internalPlaybackState.clickTrack,
                    progress = internalPlaybackState.progress,
                )
            }
            .distinctUntilChanged()
    }

    private fun currentProgress(): Double? {
        return playbackState.value?.run {
            val elapsedSinceStart = startMark.elapsedNow()
            elapsedSinceStart / clickTrack.value.durationInTime
        }
    }

    private suspend fun startImpl(clickTrack: ClickTrackWithId, startAt: Duration) = coroutineScope {
        var iterationStartsWith = if (startAt < clickTrack.value.durationInTime) startAt else Duration.ZERO

        do {
            fun Duration.toProgress(): Double = this / clickTrack.value.durationInTime

            var cueGlobalStart = Duration.ZERO

            for (cue in clickTrack.value.cues) {
                val cueLocalStart = (iterationStartsWith - cueGlobalStart).coerceAtLeast(Duration.ZERO)

                startImpl(
                    strongBeatSound = clickTrack.value.sounds.strongBeat,
                    weakBeatSound = clickTrack.value.sounds.weakBeat,
                    cue = cue,
                    startAt = cueLocalStart,
                    reportProgress = { beatTimestamp ->
                        updatePlaybackState {
                            this?.copy(
                                clickTrack = clickTrack,
                                progress = (cueGlobalStart + beatTimestamp).toProgress(),
                            )
                        }
                    }
                )

                cueGlobalStart += cue.durationAsTime
            }

            iterationStartsWith = Duration.ZERO
        } while (clickTrack.value.loop && coroutineContext.isActive)
    }

    private suspend fun startImpl(
        strongBeatSound: ClickSoundSource,
        weakBeatSound: ClickSoundSource,
        cue: Cue,
        startAt: Duration,
        reportProgress: suspend (beatTimestamp: Duration) -> Unit,
    ) {
        val totalDuration = cue.durationAsTime.takeIf { startAt < it } ?: return
        val timeSignature = cue.timeSignature
        val beatInterval = cue.bpm.interval

        var beatIndex: Int
        val initialDelay: Duration

        if (startAt % beatInterval > Duration.ZERO) {
            val nextBeatIndex = (startAt / beatInterval).toInt() + 1
            val nextBeatTimestamp = (beatInterval * nextBeatIndex).coerceAtMost(totalDuration)
            val beatRemainingDuration = nextBeatTimestamp - startAt

            reportProgress(startAt)
            delay(beatRemainingDuration)

            beatIndex = nextBeatIndex
            initialDelay = nextBeatTimestamp
        } else {
            beatIndex = (startAt / beatInterval).toInt()
            initialDelay = Duration.ZERO
        }

        tick(
            duration = (totalDuration - initialDelay - Const.TIME_EPSILON_TO_AVOID_SPURIOUS_CLICKS).coerceAtLeast(Duration.ZERO),
            interval = beatInterval,
            onTick = { passed ->
                if (pausedState.value) {
                    pausedState.filter { false }.take(1).collect()
                }

                if (beatIndex % timeSignature.noteCount == 0) {
                    soundPool.play(strongBeatSound, PlayerSoundPool.SoundPriority.STRONG)
                } else {
                    soundPool.play(weakBeatSound, PlayerSoundPool.SoundPriority.WEAK)
                }

                reportProgress(initialDelay + passed)

                ++beatIndex
            },
        )
    }

    private suspend fun updatePlaybackState(
        update: InternalPlaybackState?.() -> InternalPlaybackState?,
    ) = playbackStateMutex.withLock {
        val previous = playbackState.value
        playbackState.setValue(previous.update())
    }

    private data class InternalPlaybackState(
        val clickTrack: ClickTrackWithId,
        val progress: Double,
        val startMark: TimeMark,
    )

    private object Const {
        val TIME_EPSILON_TO_AVOID_SPURIOUS_CLICKS = 1.milliseconds
    }
}
