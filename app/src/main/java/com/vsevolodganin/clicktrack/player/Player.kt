package com.vsevolodganin.clicktrack.player

import android.os.SystemClock
import com.vsevolodganin.clicktrack.di.component.PlayerServiceScoped
import com.vsevolodganin.clicktrack.di.module.MainDispatcher
import com.vsevolodganin.clicktrack.di.module.PlayerDispatcher
import com.vsevolodganin.clicktrack.lib.ClickTrack
import com.vsevolodganin.clicktrack.lib.Cue
import com.vsevolodganin.clicktrack.lib.NoteEvent
import com.vsevolodganin.clicktrack.lib.Polyrhythm
import com.vsevolodganin.clicktrack.lib.interval
import com.vsevolodganin.clicktrack.lib.math.over
import com.vsevolodganin.clicktrack.lib.math.times
import com.vsevolodganin.clicktrack.lib.utils.collection.toRoundRobin
import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import com.vsevolodganin.clicktrack.player.PlayerImpl.PlaySchedule.SoundEvent
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundType
import com.vsevolodganin.clicktrack.sounds.model.ClickSounds
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundsId
import com.vsevolodganin.clicktrack.state.PlaybackState
import com.vsevolodganin.clicktrack.storage.ClickSoundsRepository
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.utils.coroutine.MutableNonConflatedStateFlow
import com.vsevolodganin.clicktrack.utils.coroutine.delayNanosCoroutines
import com.vsevolodganin.clicktrack.utils.coroutine.delayNanosThreadSleep
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
        val startAt = clickTrack.value.durationInTime * progress

        updatePlaybackState {
            InternalPlaybackState(
                clickTrack = clickTrack,
                startProgress = progress,
            )
        }

        playerJob?.cancel()
        playerJob = launch(playerDispatcher) {
            pausedState.setValue(false)

            clickTrack.value.play(
                startAt = startAt,
                reportProgress = { progress ->
                    updatePlaybackState {
                        this?.copy(
                            clickTrack = clickTrack,
                            startProgress = progress / clickTrack.value.durationInTime,
                        )
                    }
                }
            )

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

    private suspend fun ClickTrack.play(
        startAt: Duration,
        reportProgress: suspend (progress: Duration) -> Unit,
    ) = coroutineScope {
        @Suppress("NAME_SHADOWING")
        var startAt = if (startAt >= durationInTime) {
            if (loop) Duration.ZERO else return@coroutineScope
        } else {
            startAt
        }

        do {
            val playSchedule = cues.map { cue ->
                cue.toPlaySchedule().withDuration(cue.durationAsTime)
            }.merge().startingAt(startAt)

            reportProgress(startAt)

            GenericPatternPlayer.play(
                initialDelay = playSchedule.initialDelay,
                pattern = playSchedule.soundEvents,
                interval = { event -> event.duration },
                play = { event -> event.play() },
                delayMethod = GenericPatternPlayer.DelayMethod.THREAD_SLEEP
            )

            startAt = Duration.ZERO
        } while (loop && isActive)
    }

    private suspend fun SoundEvent.play() {
        if (pausedState.value) {
            pausedState.filter { false }.take(1).collect()
        }

        val sounds = selectedSounds()
        if (sounds != null) {
            val sound = when (type) {
                ClickSoundType.STRONG -> sounds.strongBeat
                ClickSoundType.WEAK -> sounds.weakBeat
            }
            sound?.let { soundPool.play(it, type) }
        }
    }

    private data class PlaySchedule(val soundEvents: List<SoundEvent>, val initialDelay: Duration) {
        data class SoundEvent(val type: ClickSoundType, val duration: Duration)
    }

    private fun Cue.toPlaySchedule(): PlaySchedule {
        val bpmInterval = bpm.interval
        val polyrhythm = Polyrhythm(
            pattern1 = listOf(NoteEvent(timeSignature.noteCount over 1, NoteEvent.Type.NOTE)),
            pattern2 = pattern.events,
        )

        return PlaySchedule(
            soundEvents = mutableListOf<SoundEvent>().apply {
                for (column in polyrhythm.columns) {
                    val soundType = when {
                        column.indices.contains(0) -> ClickSoundType.STRONG
                        else -> ClickSoundType.WEAK
                    }
                    this += SoundEvent(
                        type = soundType,
                        duration = bpmInterval * column.untilNext,
                    )
                }
            },
            initialDelay = bpmInterval * polyrhythm.untilFirst
        )
    }

    private fun PlaySchedule.withDuration(duration: Duration): PlaySchedule {
        if (soundEvents.isEmpty() || duration <= initialDelay) {
            return PlaySchedule(emptyList(), duration)
        }

        val soundEventsCycled = soundEvents.toRoundRobin()
        val updatedSoundEvents = mutableListOf<SoundEvent>()

        var runningDuration = initialDelay
        while (runningDuration + Const.CLICK_MIN_DELTA < duration) {
            val next = soundEventsCycled.next()
            updatedSoundEvents += next
            runningDuration += next.duration
        }

        if (runningDuration != duration) {
            val lastIndex = updatedSoundEvents.lastIndex
            updatedSoundEvents[lastIndex] = updatedSoundEvents[lastIndex].let {
                it.copy(duration = it.duration - (runningDuration - duration))
            }
        }

        return copy(soundEvents = updatedSoundEvents)
    }

    private fun PlaySchedule.startingAt(startAt: Duration): PlaySchedule {
        var runningDuration = initialDelay
        var startIndex: Int? = null
        for (index in soundEvents.indices) {
            if (runningDuration >= startAt) {
                startIndex = index
                break
            }
            runningDuration += soundEvents[index].duration
        }

        return PlaySchedule(
            soundEvents = if (startIndex == null) {
                emptyList()
            } else {
                soundEvents.subList(startIndex, soundEvents.size)
            },
            initialDelay = (runningDuration - startAt).coerceAtLeast(Duration.ZERO)
        )
    }

    private fun Iterable<PlaySchedule>.merge(): PlaySchedule {
        if (!any()) return PlaySchedule(emptyList(), Duration.ZERO)

        val first = first()
        val initialDelay = first.initialDelay
        val soundEvents = first.soundEvents.toMutableList()

        for (schedule in drop(1)) {
            val lastIndex = soundEvents.lastIndex
            soundEvents[lastIndex] = soundEvents[lastIndex].let {
                it.copy(duration = it.duration + schedule.initialDelay)
            }
            soundEvents += schedule.soundEvents
        }

        return PlaySchedule(soundEvents, initialDelay)
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

    private suspend fun updatePlaybackState(
        update: InternalPlaybackState?.() -> InternalPlaybackState?,
    ) = playbackStateMutex.withLock {
        val previous = playbackState.value
        playbackState.setValue(previous.update())
    }

    private object Const {
        val CLICK_MIN_DELTA = 1.milliseconds
    }
}

private object GenericPatternPlayer {

    enum class DelayMethod {
        THREAD_SLEEP, SUSPEND
    }

    suspend fun <T> play(
        initialDelay: Duration,
        pattern: Iterable<T>,
        interval: (T) -> Duration,
        play: suspend (element: T) -> Unit,
        delayMethod: DelayMethod,
    ) {
        play(
            initialDelay = initialDelay.toLongNanoseconds(),
            pattern = pattern,
            interval = { element -> interval(element).toLongNanoseconds() },
            play = { element -> play(element) },
            delay = delayMethod.asReference(),
        )
    }

    private suspend fun <T> play(
        initialDelay: Long,
        pattern: Iterable<T>,
        interval: (T) -> Long,
        play: suspend (element: T) -> Unit,
        delay: suspend (Long) -> Unit,
    ) {
        delay(initialDelay)
        val patternIterator = pattern.iterator()
        val startTime = nanoTime()
        var deadline = startTime
        var now: Long
        while (patternIterator.hasNext()) {
            val element = patternIterator.next()
            val elementInterval = interval(element)
            play(element)
            deadline += elementInterval
            now = nanoTime()
            delay(deadline - now)
        }
    }

    private fun DelayMethod.asReference() = when (this) {
        DelayMethod.THREAD_SLEEP -> ::delayNanosThreadSleep
        DelayMethod.SUSPEND -> ::delayNanosCoroutines
    }

    private fun nanoTime() = SystemClock.elapsedRealtimeNanos()
}
