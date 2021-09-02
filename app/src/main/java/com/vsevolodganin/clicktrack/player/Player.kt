package com.vsevolodganin.clicktrack.player

import android.os.SystemClock
import com.vsevolodganin.clicktrack.di.component.PlayerServiceScoped
import com.vsevolodganin.clicktrack.di.module.MainDispatcher
import com.vsevolodganin.clicktrack.di.module.PlayerDispatcher
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
import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import com.vsevolodganin.clicktrack.model.PlayableId
import com.vsevolodganin.clicktrack.model.PlayableProgress
import com.vsevolodganin.clicktrack.model.TwoLayerPolyrhythmId
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundType
import com.vsevolodganin.clicktrack.sounds.model.ClickSounds
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundsId
import com.vsevolodganin.clicktrack.storage.ClickSoundsRepository
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.utils.collection.sequence.prefetch
import com.vsevolodganin.clicktrack.utils.coroutine.delayNanosCoroutines
import com.vsevolodganin.clicktrack.utils.coroutine.delayNanosThreadSleep
import com.vsevolodganin.clicktrack.utils.grabIf
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

interface Player {
    suspend fun start(clickTrack: ClickTrackWithId, atProgress: Double? = null, soundsId: ClickSoundsId? = null)
    suspend fun start(twoLayerPolyrhythm: TwoLayerPolyrhythm, atProgress: Double? = null, soundsId: ClickSoundsId? = null)
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
    private var playbackState = MutableStateFlow<InternalPlaybackState?>(null)
    private var pausedState = MutableStateFlow(false)

    override suspend fun start(clickTrack: ClickTrackWithId, atProgress: Double?, soundsId: ClickSoundsId?) = withContext(mainDispatcher) {
        val duration = clickTrack.value.durationInTime

        if (duration == Duration.ZERO) {
            Timber.w("Tried to play track with zero duration, exiting")
            stop()
            return@withContext
        }

        val currentPlayback = playbackState.value
        val progress = atProgress
            ?: grabIf(clickTrack.id == currentPlayback?.id) { currentPlayback?.currentProgress() }
            ?: 0.0
        val startAt = duration * progress

        playerJob?.cancel()
        playerJob = launch(playerDispatcher) {
            pausedState.value = false

            clickTrack.value.play(
                startAt = startAt,
                reportProgress = { progress ->
                    playbackState.value = InternalPlaybackState(
                        id = clickTrack.id,
                        duration = duration,
                        progress = progress / duration,
                    )
                },
                soundsSelector = soundsSelector(soundsId)
            )

            stop()
        }
    }

    override suspend fun start(twoLayerPolyrhythm: TwoLayerPolyrhythm, atProgress: Double?, soundsId: ClickSoundsId?) = withContext(mainDispatcher) {
        val duration = twoLayerPolyrhythm.durationInTime

        if (duration == Duration.ZERO) {
            Timber.w("Tried to play polyrhythm with zero duration, exiting")
            stop()
            return@withContext
        }

        val currentPlayback = playbackState.value
        val progress = atProgress
            ?: grabIf(TwoLayerPolyrhythmId == currentPlayback?.id) { currentPlayback?.currentProgress() }
            ?: 0.0
        val startAt = duration * progress

        playerJob?.cancel()
        playerJob = launch(playerDispatcher) {
            pausedState.value = false

            twoLayerPolyrhythm.play(
                startAt = startAt,
                reportProgress = { progress ->
                    playbackState.value = InternalPlaybackState(
                        id = TwoLayerPolyrhythmId,
                        duration = duration,
                        progress = progress / duration,
                    )
                },
                soundsSelector = soundsSelector(soundsId)
            )

            stop()
        }
    }

    override suspend fun pause() {
        pausedState.value = true
    }

    override suspend fun stop(): Unit = withContext(mainDispatcher) {
        playerJob?.cancel()
        playerJob = null
        withContext(playerDispatcher) {
            playbackState.value = null
            soundPool.stopAll()
        }
    }

    override fun playbackState(): Flow<PlaybackState?> {
        return playbackState
            .map { internalPlaybackState ->
                internalPlaybackState ?: return@map null
                PlaybackState(
                    id = internalPlaybackState.id,
                    progress = PlayableProgress(internalPlaybackState.currentProgress()),
                )
            }
    }

    private suspend fun ClickTrack.play(
        startAt: Duration,
        reportProgress: (progress: Duration) -> Unit,
        soundsSelector: () -> ClickSounds?,
    ) = coroutineScope {
        @Suppress("NAME_SHADOWING") // Because that looks better
        val startAt = if (startAt >= durationInTime) {
            if (loop) Duration.ZERO else return@coroutineScope
        } else {
            startAt
        }

        val schedule = cues.asSequence()
            .flatMap { cue ->
                cue
                    .toPlayerSchedule(soundsSelector)
                    .withDuration(cue.durationAsTime)
            }
            .reportProgressAtTheBeginning(reportProgress, Duration.ZERO)
            .loop(loop)
            .let {
                if (startAt > Duration.ZERO) {
                    it.startingAt(startAt).reportProgressAtTheBeginning(reportProgress, startAt)
                } else {
                    it
                }
            }
            .prefetch(Const.PREFETCH_SIZE)

        PlayerScheduleSequencer.play(
            schedule = schedule,
            delayMethod = PlayerScheduleSequencer.DelayMethod.THREAD_SLEEP
        )
    }

    private suspend fun TwoLayerPolyrhythm.play(
        startAt: Duration,
        reportProgress: (progress: Duration) -> Unit,
        soundsSelector: () -> ClickSounds?,
    ) = coroutineScope {
        @Suppress("NAME_SHADOWING") // Because that looks better
        val startAt = if (startAt >= durationInTime) {
            Duration.ZERO
        } else {
            startAt
        }

        val schedule = toPlayerSchedule(soundsSelector)
            .reportProgressAtTheBeginning(reportProgress, Duration.ZERO)
            .toList()
            .asSequence()
            .loop(true)
            .let {
                if (startAt > Duration.ZERO) {
                    it.startingAt(startAt).reportProgressAtTheBeginning(reportProgress, startAt)
                } else {
                    it
                }
            }
            .prefetch(Const.PREFETCH_SIZE)

        PlayerScheduleSequencer.play(
            schedule = schedule,
            delayMethod = PlayerScheduleSequencer.DelayMethod.THREAD_SLEEP,
        )
    }

    private fun Cue.toPlayerSchedule(soundsSelector: () -> ClickSounds?): PlayerSchedule {
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
                    soundEvent(
                        type = soundType,
                        duration = bpmInterval * column.untilNext,
                        waitResume = {
                            if (pausedState.value) {
                                pausedState.filter { false }.take(1).collect()
                            }
                        },
                        selectedSounds = soundsSelector,
                        soundPool = soundPool,
                    )
                )
            }
        }
    }

    private fun TwoLayerPolyrhythm.toPlayerSchedule(soundsSelector: () -> ClickSounds?): PlayerSchedule {
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

                for (soundType in soundTypes) {
                    yield(
                        soundEvent(
                            type = soundType,
                            duration = Duration.ZERO,
                            waitResume = {
                                if (pausedState.value) {
                                    pausedState.filter { false }.take(1).collect()
                                }
                            },
                            selectedSounds = soundsSelector,
                            soundPool = soundPool,
                        )
                    )
                }

                yield(delayEvent(duration = bpmInterval * column.untilNext))
            }
        }
    }

    private fun PlayerSchedule.withDuration(duration: Duration): PlayerSchedule {
        return sequence {
            val soundEventsCycledIterator = toRoundRobin().iterator()

            var runningDuration = Duration.ZERO
            while (true) {
                val next = soundEventsCycledIterator.next()
                runningDuration += next.interval

                if (runningDuration + Const.CLICK_MIN_DELTA >= duration) {
                    if (runningDuration != duration) {
                        yield(next.copy(interval = next.interval - (runningDuration - duration)))
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

    private fun PlayerSchedule.startingAt(startAt: Duration): PlayerSchedule {
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
            yield(delayEvent((runningDuration - startAt).coerceAtLeast(Duration.ZERO)))
            yieldAll(drop(dropCount))
        }
    }

    private fun PlayerSchedule.reportProgressAtTheBeginning(
        reportProgress: (progress: Duration) -> Unit,
        progress: Duration,
    ) = sequence {
        yield(actionEvent { reportProgress(progress) })
        yieldAll(this@reportProgressAtTheBeginning)
    }

    private fun PlayerSchedule.loop(loop: Boolean): PlayerSchedule {
        return if (loop) toRoundRobin() else this
    }

    private suspend fun CoroutineScope.soundsSelector(soundsId: ClickSoundsId?): () -> ClickSounds? {
        return if (soundsId != null) {
            soundsById(soundsId)
        } else {
            userSelectedSounds()
        }
            .onEach { sounds ->
                soundPool.stopAll()
                sounds?.asIterable?.forEach(soundPool::warmup)
            }
            .stateIn(scope = this)::value
    }

    private fun userSelectedSounds(): Flow<ClickSounds?> {
        return userPreferencesRepository.selectedSoundsId.flow
            .flatMapLatest(::soundsById)
    }

    private fun soundsById(soundsId: ClickSoundsId): Flow<ClickSounds?> {
        return when (soundsId) {
            is ClickSoundsId.Builtin -> flowOf(soundsId.value.sounds)
            is ClickSoundsId.Database -> clickSoundsRepository.getById(soundsId).map { it?.value }
        }
    }

    private class InternalPlaybackState(
        val id: PlayableId,
        val duration: Duration,
        val progress: Double,
    ) {
        val startedAt: TimeMark = TimeSource.Monotonic.markNow()

        fun currentProgress(): Double {
            val elapsedSinceStart = startedAt.elapsedNow()
            return progress + elapsedSinceStart / duration
        }
    }

    private object Const {
        val CLICK_MIN_DELTA = Duration.milliseconds(1)
        const val PREFETCH_SIZE = 100
    }
}

private data class PlayerScheduleEvent(
    val interval: Duration = Duration.ZERO,
    val action: (suspend () -> Unit)? = null,
)

private typealias PlayerSchedule = Sequence<PlayerScheduleEvent>

private fun soundEvent(
    type: ClickSoundType,
    duration: Duration,
    waitResume: suspend () -> Unit,
    selectedSounds: () -> ClickSounds?,
    soundPool: PlayerSoundPool,
) = PlayerScheduleEvent(
    interval = duration,
    action = {
        waitResume()

        val sounds = selectedSounds()
        if (sounds != null) {
            val sound = when (type) {
                ClickSoundType.STRONG -> sounds.strongBeat
                ClickSoundType.WEAK -> sounds.weakBeat
            }
            sound?.let(soundPool::play)
        }
    }
)

private fun delayEvent(duration: Duration) = PlayerScheduleEvent(interval = duration)

private fun actionEvent(action: suspend () -> Unit) = PlayerScheduleEvent(action = action)

private object PlayerScheduleSequencer {

    enum class DelayMethod {
        THREAD_SLEEP, SUSPEND
    }

    suspend fun play(
        schedule: PlayerSchedule,
        delayMethod: DelayMethod,
    ) {
        val delay = delayMethod.referenceToMethod()
        val iterator = schedule.iterator()
        val startTime = nanoTime()
        var deadline = startTime
        var now: Long
        while (iterator.hasNext()) {
            val event = iterator.next()
            event.action?.invoke()
            val interval = event.interval
            deadline += interval.inWholeNanoseconds
            now = nanoTime()
            delay(deadline - now)
        }
    }

    private fun DelayMethod.referenceToMethod() = when (this) {
        DelayMethod.THREAD_SLEEP -> ::delayNanosThreadSleep
        DelayMethod.SUSPEND -> ::delayNanosCoroutines
    }

    private fun nanoTime() = SystemClock.elapsedRealtimeNanos()
}
