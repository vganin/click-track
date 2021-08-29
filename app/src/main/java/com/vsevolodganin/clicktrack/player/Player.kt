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
import com.vsevolodganin.clicktrack.utils.coroutine.MutableNonConflatedStateFlow
import com.vsevolodganin.clicktrack.utils.coroutine.delayNanosCoroutines
import com.vsevolodganin.clicktrack.utils.coroutine.delayNanosThreadSleep
import com.vsevolodganin.clicktrack.utils.grabIf
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
    private val playbackStateMutex = Mutex()
    private var playbackState = MutableNonConflatedStateFlow<InternalPlaybackState?>(null)
    private var pausedState = MutableNonConflatedStateFlow(false)

    override suspend fun start(clickTrack: ClickTrackWithId, atProgress: Double?, soundsId: ClickSoundsId?) = withContext(mainDispatcher) {
        if (clickTrack.value.durationInTime == Duration.ZERO) {
            Timber.w("Tried to play track with zero duration, exiting")
            stop()
            return@withContext
        }

        val currentPlayback = playbackState.value
        val progress = atProgress
            ?: grabIf(clickTrack.id == currentPlayback?.id) { currentPlayback?.currentProgress() }
            ?: 0.0
        val startAt = clickTrack.value.durationInTime * progress

        updatePlaybackState {
            InternalPlaybackState(
                id = clickTrack.id,
                duration = clickTrack.value.durationInTime,
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
                            startProgress = progress / clickTrack.value.durationInTime,
                        )
                    }
                },
                soundsSelector = soundsId?.let { { soundById(it) } }
                    ?: ::userSelectedSounds
            )

            stop()
        }
    }

    override suspend fun start(twoLayerPolyrhythm: TwoLayerPolyrhythm, atProgress: Double?, soundsId: ClickSoundsId?) = withContext(mainDispatcher) {
        if (twoLayerPolyrhythm.durationInTime == Duration.ZERO) {
            Timber.w("Tried to play polyrhythm with zero duration, exiting")
            stop()
            return@withContext
        }

        val currentPlayback = playbackState.value
        val progress = atProgress
            ?: grabIf(TwoLayerPolyrhythmId == currentPlayback?.id) { currentPlayback?.currentProgress() }
            ?: 0.0
        val startAt = twoLayerPolyrhythm.durationInTime * progress

        updatePlaybackState {
            InternalPlaybackState(
                id = TwoLayerPolyrhythmId,
                duration = twoLayerPolyrhythm.durationInTime,
                startProgress = progress,
            )
        }

        playerJob?.cancel()
        playerJob = launch(playerDispatcher) {
            pausedState.setValue(false)

            twoLayerPolyrhythm.play(
                startAt = startAt,
                reportProgress = { progress ->
                    updatePlaybackState {
                        this?.copy(
                            startProgress = progress / twoLayerPolyrhythm.durationInTime,
                        )
                    }
                },
                soundsSelector = soundsId?.let { { soundById(it) } }
                    ?: ::userSelectedSounds
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
        withContext(playerDispatcher) {
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
        reportProgress: suspend (progress: Duration) -> Unit,
        soundsSelector: suspend () -> ClickSounds?,
    ) = coroutineScope {
        @Suppress("NAME_SHADOWING") // Because that looks better
        val startAt = if (startAt >= durationInTime) {
            if (loop) Duration.ZERO else return@coroutineScope
        } else {
            startAt
        }

        val schedule = cues
            .asSequence()
            .flatMap { cue -> cue.toPlayerSchedule(soundsSelector).withDuration(cue.durationAsTime) }
            .reportProgress(reportProgress)
            .loop(loop)
            .run {
                if (startAt > Duration.ZERO) {
                    startingAt(startAt)
                        .reportProgress { passed -> reportProgress(startAt + passed) }
                } else {
                    this@run
                }
            }

        GenericPatternPlayer.play(
            pattern = schedule,
            interval = { event -> event.interval },
            play = { event -> event.play() },
            delayMethod = GenericPatternPlayer.DelayMethod.THREAD_SLEEP
        )
    }

    private suspend fun TwoLayerPolyrhythm.play(
        startAt: Duration,
        reportProgress: suspend (progress: Duration) -> Unit,
        soundsSelector: suspend () -> ClickSounds?,
    ) = coroutineScope {
        @Suppress("NAME_SHADOWING") // Because that looks better
        val startAt = if (startAt >= durationInTime) {
            Duration.ZERO
        } else {
            startAt
        }

        val schedule = toPlayerSchedule(soundsSelector)
            .reportProgress(reportProgress)
            .loop(true)
            .run {
                if (startAt > Duration.ZERO) {
                    startingAt(startAt)
                        .reportProgress { passed -> reportProgress(startAt + passed) }
                } else {
                    this@run
                }
            }

        GenericPatternPlayer.play(
            pattern = schedule,
            interval = { event -> event.interval },
            play = { event -> event.play() },
            delayMethod = GenericPatternPlayer.DelayMethod.THREAD_SLEEP
        )
    }

    private fun Cue.toPlayerSchedule(soundsSelector: suspend () -> ClickSounds?): PlayerSchedule {
        val tempo = bpm
        val polyrhythm = AbstractPolyrhythm(
            pattern1 = listOf(NoteEvent(timeSignature.noteCount.toRational(), NoteEvent.Type.NOTE)),
            pattern2 = pattern.events,
        )

        return sequence {
            val bpmInterval = tempo.interval

            if (polyrhythm.untilFirst > Rational.ZERO) {
                yield(DelayEvent(bpmInterval * polyrhythm.untilFirst))
            }

            for (column in polyrhythm.columns) {
                val soundType = when {
                    column.indices.contains(0) -> ClickSoundType.STRONG
                    else -> ClickSoundType.WEAK
                }

                yield(
                    SoundEvent(
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

    private fun TwoLayerPolyrhythm.toPlayerSchedule(soundsSelector: suspend () -> ClickSounds?): PlayerSchedule {
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
                yield(DelayEvent(bpmInterval * polyrhythm.untilFirst))
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
                        SoundEvent(
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

                yield(DelayEvent(duration = bpmInterval * column.untilNext))
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
                        yield(next.withInterval(interval = next.interval - (runningDuration - duration)))
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
            yield(DelayEvent((runningDuration - startAt).coerceAtLeast(Duration.ZERO)))
            yieldAll(drop(dropCount))
        }
    }

    private fun PlayerSchedule.reportProgress(reportProgress: suspend (progress: Duration) -> Unit): PlayerSchedule {
        return sequence {
            yield(ReportProgressEvent(TimeSource.Monotonic.markNow(), reportProgress))
            yieldAll(this@reportProgress)
        }
    }

    private fun PlayerSchedule.loop(loop: Boolean): PlayerSchedule {
        return if (loop) toRoundRobin() else this
    }

    private suspend fun userSelectedSounds(): ClickSounds? {
        return soundById(userPreferencesRepository.selectedSoundsId.flow.first())
    }

    private suspend fun soundById(soundsId: ClickSoundsId): ClickSounds? {
        return when (soundsId) {
            is ClickSoundsId.Builtin -> soundsId.value.sounds
            is ClickSoundsId.Database -> clickSoundsRepository.getById(soundsId).firstOrNull()?.value
        }
    }

    private data class InternalPlaybackState(
        val id: PlayableId,
        val duration: Duration,
        val startProgress: Double,
    ) {
        val startedAt: TimeMark = TimeSource.Monotonic.markNow()

        fun currentProgress(): Double {
            val elapsedSinceStart = startedAt.elapsedNow()
            return startProgress + elapsedSinceStart / duration
        }
    }

    private suspend fun updatePlaybackState(
        update: InternalPlaybackState?.() -> InternalPlaybackState?,
    ) = playbackStateMutex.withLock {
        val previous = playbackState.value
        playbackState.setValue(previous.update())
    }

    private object Const {
        val CLICK_MIN_DELTA = Duration.milliseconds(1)
    }
}

private interface PlayerScheduleEvent {
    val interval: Duration
    suspend fun play()
    fun withInterval(interval: Duration): PlayerScheduleEvent
}

private typealias PlayerSchedule = Sequence<PlayerScheduleEvent>

private data class SoundEvent(
    private val type: ClickSoundType,
    private val duration: Duration,
    private val waitResume: suspend () -> Unit,
    private val selectedSounds: suspend () -> ClickSounds?,
    private val soundPool: PlayerSoundPool,
) : PlayerScheduleEvent {

    override val interval: Duration get() = duration

    override suspend fun play() {
        waitResume()

        val sounds = selectedSounds()
        if (sounds != null) {
            val sound = when (type) {
                ClickSoundType.STRONG -> sounds.strongBeat
                ClickSoundType.WEAK -> sounds.weakBeat
            }
            sound?.let { soundPool.play(it) }
        }
    }

    override fun withInterval(interval: Duration): PlayerScheduleEvent {
        return copy(duration = interval)
    }
}

private data class DelayEvent(private val duration: Duration) : PlayerScheduleEvent {

    override val interval: Duration get() = duration

    override suspend fun play() = Unit

    override fun withInterval(interval: Duration): PlayerScheduleEvent {
        return copy(duration = interval)
    }
}

private data class ReportProgressEvent(
    private val timeMark: TimeMark,
    private val reportProgress: suspend (progress: Duration) -> Unit,
    override val interval: Duration = Duration.ZERO,
) : PlayerScheduleEvent {

    override suspend fun play() {
        reportProgress(timeMark.elapsedNow())
    }

    override fun withInterval(interval: Duration): PlayerScheduleEvent {
        return copy(interval = interval)
    }
}

private object GenericPatternPlayer {

    enum class DelayMethod {
        THREAD_SLEEP, SUSPEND
    }

    suspend fun <T> play(
        pattern: Sequence<T>,
        interval: (T) -> Duration,
        play: suspend (element: T) -> Unit,
        delayMethod: DelayMethod,
    ) {
        play(
            pattern = pattern,
            interval = { element -> interval(element).inWholeNanoseconds },
            play = { element -> play(element) },
            delay = delayMethod.asReference(),
        )
    }

    private suspend fun <T> play(
        pattern: Sequence<T>,
        interval: (T) -> Long,
        play: suspend (element: T) -> Unit,
        delay: suspend (Long) -> Unit,
    ) {
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
