package com.vsevolodganin.clicktrack.player

import android.os.SystemClock
import androidx.compose.ui.util.fastForEach
import com.vsevolodganin.clicktrack.di.component.PlayerServiceScoped
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
import com.vsevolodganin.clicktrack.model.PlayableProgressTimeSource
import com.vsevolodganin.clicktrack.model.TwoLayerPolyrhythmId
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundType
import com.vsevolodganin.clicktrack.sounds.model.ClickSounds
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundsId
import com.vsevolodganin.clicktrack.storage.ClickSoundsRepository
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.utils.collection.sequence.prefetch
import com.vsevolodganin.clicktrack.utils.coroutine.delayTillDeadlineUsingCoroutines
import com.vsevolodganin.clicktrack.utils.coroutine.delayTillDeadlineUsingSuspendAndSpinLock
import com.vsevolodganin.clicktrack.utils.coroutine.delayTillDeadlineUsingThreadSleep
import com.vsevolodganin.clicktrack.utils.coroutine.delayTillDeadlineUsingThreadSleepAndSpinLock
import com.vsevolodganin.clicktrack.utils.grabIf
import javax.inject.Inject
import kotlin.time.Duration
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
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
    @PlayerDispatcher private val playerDispatcher: CoroutineDispatcher,
    private val clickSoundsRepository: ClickSoundsRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : Player {

    private var playerJob: Job? = null
    private val playbackState = MutableStateFlow<InternalPlaybackState?>(null)
    private val pausedState = MutableStateFlow(false)

    override suspend fun start(
        clickTrack: ClickTrackWithId,
        atProgress: Double?,
        soundsId: ClickSoundsId?,
    ) = coroutineScope {
        playerJob?.cancel()
        playerJob = launchPlayer {
            val duration = clickTrack.value.durationInTime

            if (duration == Duration.ZERO) {
                Timber.w("Tried to play track with zero duration, exiting")
                return@launchPlayer
            }

            val currentPlayback = playbackState.value
            val progress = atProgress
                ?: grabIf(clickTrack.id == currentPlayback?.id) { currentPlayback?.calculateCurrentProgress() }
                ?: 0.0
            val startAt = duration * progress

            pausedState.value = false

            clickTrack.value.play(
                startAt = startAt,
                reportProgress = {
                    playbackState.value = InternalPlaybackState(
                        id = clickTrack.id,
                        duration = duration,
                        progress = it,
                    )
                },
                soundsSelector = soundsSelector(soundsId)
            )
        }
    }

    override suspend fun start(
        twoLayerPolyrhythm: TwoLayerPolyrhythm,
        atProgress: Double?,
        soundsId: ClickSoundsId?,
    ) = coroutineScope {
        playerJob?.cancel()
        playerJob = launchPlayer {
            val duration = twoLayerPolyrhythm.durationInTime

            if (duration == Duration.ZERO) {
                Timber.w("Tried to play polyrhythm with zero duration, exiting")
                return@launchPlayer
            }

            val currentPlayback = playbackState.value
            val progress = atProgress
                ?: grabIf(TwoLayerPolyrhythmId == currentPlayback?.id) { currentPlayback?.calculateCurrentProgress() }
                ?: 0.0
            val startAt = duration * progress

            pausedState.value = false

            twoLayerPolyrhythm.play(
                startAt = startAt,
                reportProgress = {
                    playbackState.value = InternalPlaybackState(
                        id = TwoLayerPolyrhythmId,
                        duration = duration,
                        progress = it,
                    )
                },
                soundsSelector = soundsSelector(soundsId)
            )
        }
    }

    private fun CoroutineScope.launchPlayer(block: suspend () -> Unit): Job {
        return launch(playerDispatcher) {
            block()

            // Stop on successful block exit only. If coroutine was cancelled,
            // the client should stop all by himself
            playbackState.value = null
            soundPool.stopAll()
        }
    }

    override suspend fun pause() {
        pausedState.value = true
    }

    override suspend fun stop() = coroutineScope {
        playerJob?.cancel()
        playerJob = launch(playerDispatcher) {
            playbackState.value = null

            // Delaying superfluous sound pool stops in order to avoid
            // race condition on some Android versions which causes
            // audio stream to shut down without any notification.
            // For more info see https://github.com/google/oboe/issues/1315
            delay(500)
            soundPool.stopAll()
        }
    }

    override fun playbackState(): Flow<PlaybackState?> {
        return playbackState
            .map { internalPlaybackState ->
                internalPlaybackState ?: return@map null
                PlaybackState(
                    id = internalPlaybackState.id,
                    progress = PlayableProgress(
                        value = internalPlaybackState.progress,
                        duration = internalPlaybackState.duration,
                        generationTimeMark = internalPlaybackState.startedAt,
                    ),
                )
            }
    }

    private suspend fun ClickTrack.play(
        startAt: Duration,
        reportProgress: (progress: Duration) -> Unit,
        soundsSelector: () -> ClickSounds?,
    ) {
        @Suppress("NAME_SHADOWING") // Because that looks better
        val startAt = if (startAt >= durationInTime) {
            if (loop) Duration.ZERO else return
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

        PlayerScheduleSequencer.play(schedule)
    }

    private suspend fun TwoLayerPolyrhythm.play(
        startAt: Duration,
        reportProgress: (progress: Duration) -> Unit,
        soundsSelector: () -> ClickSounds?,
    ) {
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

        PlayerScheduleSequencer.play(schedule)
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

                val action = suspend {
                    soundTypes.fastForEach { soundType ->
                        playSound(
                            type = soundType,
                            waitResume = {
                                if (pausedState.value) {
                                    pausedState.filter { false }.take(1).collect()
                                }
                            },
                            selectedSounds = soundsSelector,
                            soundPool = soundPool,
                        )
                    }
                }

                yield(
                    PlayerScheduleEvent(
                        interval = bpmInterval * column.untilNext,
                        action = action
                    )
                )
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
    ) = mapIndexed { index, event ->
        if (index == 0) {
            PlayerScheduleEvent(
                interval = event.interval,
                action = {
                    event.action()
                    reportProgress(progress)
                }
            )
        } else {
            event
        }
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
        val progress: Duration,
    ) {
        val startedAt = PlayableProgressTimeSource.markNow()

        fun calculateCurrentProgress(): Double {
            val elapsedSinceStart = startedAt.elapsedNow()
            return (progress + elapsedSinceStart) / duration
        }
    }

    private object Const {
        val CLICK_MIN_DELTA = Duration.milliseconds(1)
        const val PREFETCH_SIZE = 100
    }
}

private data class PlayerScheduleEvent(
    val interval: Duration = Duration.ZERO,
    val action: suspend () -> Unit = {},
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
        playSound(
            type = type,
            waitResume = waitResume,
            selectedSounds = selectedSounds,
            soundPool = soundPool,
        )
    }
)

private suspend fun playSound(
    type: ClickSoundType,
    waitResume: suspend () -> Unit,
    selectedSounds: () -> ClickSounds?,
    soundPool: PlayerSoundPool,
) {
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

private fun delayEvent(duration: Duration) = PlayerScheduleEvent(interval = duration)

private object PlayerScheduleSequencer {

    enum class DelayMethod {
        THREAD_SLEEP, SUSPEND, THREAD_SLEEP_SPIN_LOCK, SUSPEND_SPIN_LOCK
    }

    suspend fun play(
        schedule: PlayerSchedule,
        delayMethod: DelayMethod = DelayMethod.THREAD_SLEEP_SPIN_LOCK,
    ) {
        val thisCoroutineJob = currentCoroutineContext()[Job] ?: return
        val delay = delayMethod.referenceToMethod()
        val iterator = schedule.iterator()
        val startTime = nanoTime()
        var deadline = startTime
        while (iterator.hasNext()) {
            thisCoroutineJob.ensureActive()
            val event = iterator.next()
            event.action()
            val interval = event.interval
            deadline += interval.inWholeNanoseconds
            delay(deadline)
        }
    }

    private fun DelayMethod.referenceToMethod() = when (this) {
        DelayMethod.THREAD_SLEEP -> ::delayTillDeadlineUsingThreadSleep
        DelayMethod.SUSPEND -> ::delayTillDeadlineUsingCoroutines
        DelayMethod.THREAD_SLEEP_SPIN_LOCK -> ::delayTillDeadlineUsingThreadSleepAndSpinLock
        DelayMethod.SUSPEND_SPIN_LOCK -> ::delayTillDeadlineUsingSuspendAndSpinLock
    }

    private fun nanoTime() = SystemClock.elapsedRealtimeNanos()
}
