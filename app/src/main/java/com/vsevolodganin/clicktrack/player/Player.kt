package com.vsevolodganin.clicktrack.player

import com.vsevolodganin.clicktrack.di.component.PlayerServiceScoped
import com.vsevolodganin.clicktrack.di.module.MainDispatcher
import com.vsevolodganin.clicktrack.di.module.PlayerDispatcher
import com.vsevolodganin.clicktrack.lib.Cue
import com.vsevolodganin.clicktrack.lib.NoteEvent
import com.vsevolodganin.clicktrack.lib.Polyrhythm
import com.vsevolodganin.clicktrack.lib.interval
import com.vsevolodganin.clicktrack.lib.math.Rational
import com.vsevolodganin.clicktrack.lib.math.ZERO
import com.vsevolodganin.clicktrack.lib.math.compareTo
import com.vsevolodganin.clicktrack.lib.math.over
import com.vsevolodganin.clicktrack.lib.math.times
import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundPriority
import com.vsevolodganin.clicktrack.sounds.model.ClickSounds
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundsId
import com.vsevolodganin.clicktrack.state.PlaybackState
import com.vsevolodganin.clicktrack.storage.ClickSoundsRepository
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.utils.coroutine.MutableNonConflatedStateFlow
import com.vsevolodganin.clicktrack.utils.coroutine.tick
import com.vsevolodganin.clicktrack.utils.grabIf
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource
import kotlin.time.milliseconds
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

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
    private val clickSoundsRepository: ClickSoundsRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : Player {

    private var playerJob: Job? = null
    private val playbackStateMutex = Mutex()
    private var playbackState = MutableNonConflatedStateFlow<InternalPlaybackState?>(null)
    private var pausedState = MutableNonConflatedStateFlow(false)

    override suspend fun start(clickTrack: ClickTrackWithId, atProgress: Double?): Unit = withContext(mainDispatcher) {
        val currentPlayback = playbackState.value
        val progress = atProgress
            ?: grabIf(clickTrack.id == currentPlayback?.clickTrack?.id) { currentPlayback?.currentProgress() }
            ?: 0.0
        val startAtTime = clickTrack.value.durationInTime * progress

        updatePlaybackState {
            InternalPlaybackState(
                clickTrack = clickTrack,
                startProgress = progress,
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
                    progress = internalPlaybackState.currentProgress(),
                )
            }
            .distinctUntilChanged()
    }

    private suspend fun startImpl(clickTrack: ClickTrackWithId, startAt: Duration) = coroutineScope {
        var iterationStartsWith = if (startAt < clickTrack.value.durationInTime) startAt else Duration.ZERO

        do {
            fun Duration.toProgress(): Double = this / clickTrack.value.durationInTime

            var cueGlobalStart = Duration.ZERO

            for (cue in clickTrack.value.cues) {
                val cueLocalStart = (iterationStartsWith - cueGlobalStart).coerceAtLeast(Duration.ZERO)

                startImpl(
                    cue = cue,
                    startAt = cueLocalStart,
                    reportProgress = { beatTimestamp ->
                        updatePlaybackState {
                            this?.copy(
                                clickTrack = clickTrack,
                                startProgress = (cueGlobalStart + beatTimestamp).toProgress(),
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
        cue: Cue,
        startAt: Duration,
        reportProgress: suspend (beatTimestamp: Duration) -> Unit,
    ) {
        val totalDuration = cue.durationAsTime

        if (startAt >= totalDuration) {
            return
        }

        val playEvents = cue.toPlayEvents().startingAt(startAt)

        reportProgress(startAt)

        playEvents.playLoop(totalDuration - startAt)
    }

    private suspend fun List<PlayEvent>.playLoop(forDuration: Duration) {
        tick(
            duration = (forDuration - Const.TIME_EPSILON_TO_AVOID_SPURIOUS_CLICKS).coerceAtLeast(Duration.ZERO),
            objects = this,
            intervalSelector = { it.duration },
            onTick = { _, playEvent ->
                if (pausedState.value) {
                    pausedState.filter { false }.take(1).collect()
                }

                when (playEvent) {
                    is PlayEvent.Rest -> return@tick
                    is PlayEvent.Sound -> {
                        val priority = playEvent.priority
                        val sounds = selectedSounds()
                        if (sounds != null) {
                            val sound = when (priority) {
                                ClickSoundPriority.STRONG -> sounds.strongBeat
                                ClickSoundPriority.WEAK -> sounds.weakBeat
                            }
                            sound?.let { soundPool.play(it, priority) }
                        }
                    }
                }
            },
        )
    }

    private sealed class PlayEvent {
        abstract val duration: Duration

        data class Sound(val priority: ClickSoundPriority, override val duration: Duration) : PlayEvent()
        data class Rest(override val duration: Duration) : PlayEvent()
    }

    private fun Cue.toPlayEvents(): List<PlayEvent> {
        val result = mutableListOf<PlayEvent>()
        val bpmInterval = bpm.interval
        val polyrhythm = Polyrhythm(
            pattern1 = listOf(NoteEvent(timeSignature.noteCount over 1, NoteEvent.Type.NOTE)),
            pattern2 = pattern.events,
        )

        if (polyrhythm.untilFirst > Rational.ZERO) {
            result += PlayEvent.Rest(bpmInterval * polyrhythm.untilFirst)
        }

        var runningDuration = Duration.ZERO
        for (column in polyrhythm.columns) {
            runningDuration += bpmInterval * column.untilNext
            val priority = when {
                column.indices.contains(0) -> ClickSoundPriority.STRONG
                else -> ClickSoundPriority.WEAK
            }
            result += PlayEvent.Sound(
                priority = priority,
                duration = runningDuration,
            )
            runningDuration = Duration.ZERO
        }

        return result
    }

    private fun List<PlayEvent>.startingAt(startAt: Duration): List<PlayEvent> {
        var runningTime = Duration.ZERO
        var startIndex: Int? = null
        for (index in indices) {
            if (runningTime >= startAt) {
                startIndex = index
                break
            }
            runningTime += this[index].duration
        }
        startIndex ?: return emptyList()

        val fromStartToFirstEvent = runningTime - startAt
        val initialRest = if (fromStartToFirstEvent > Const.TIME_EPSILON_TO_AVOID_SPURIOUS_CLICKS) {
            PlayEvent.Rest(duration = fromStartToFirstEvent)
        } else {
            null
        }

        return listOfNotNull(initialRest) + subList(startIndex, size)
    }

    private suspend fun updatePlaybackState(
        update: InternalPlaybackState?.() -> InternalPlaybackState?,
    ) = playbackStateMutex.withLock {
        val previous = playbackState.value
        playbackState.setValue(previous.update())
    }

    private suspend fun selectedSounds(): ClickSounds? {
        return when (val soundsId = userPreferencesRepository.selectedSoundsId) {
            is ClickSoundsId.Builtin -> soundsId.value.sounds
            is ClickSoundsId.Database -> clickSoundsRepository.getById(soundsId).firstOrNull()?.value
        }
    }

    private data class InternalPlaybackState(
        val clickTrack: ClickTrackWithId,
        val startProgress: Double,
    ) {
        val startedAt: TimeMark = TimeSource.Monotonic.markNow()

        fun currentProgress(): Double {
            val elapsedSinceStart = startedAt.elapsedNow()
            return startProgress + elapsedSinceStart / clickTrack.value.durationInTime
        }
    }

    private object Const {
        val TIME_EPSILON_TO_AVOID_SPURIOUS_CLICKS = 1.milliseconds
    }
}
