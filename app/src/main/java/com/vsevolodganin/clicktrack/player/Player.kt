package com.vsevolodganin.clicktrack.player

import android.os.SystemClock
import com.vsevolodganin.clicktrack.di.component.PlayerServiceScoped
import com.vsevolodganin.clicktrack.di.module.PlayerDispatcher
import com.vsevolodganin.clicktrack.model.ClickTrack
import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import com.vsevolodganin.clicktrack.model.PlayProgress
import com.vsevolodganin.clicktrack.model.PlayableId
import com.vsevolodganin.clicktrack.model.PlayableProgressTimeSource
import com.vsevolodganin.clicktrack.model.TwoLayerPolyrhythm
import com.vsevolodganin.clicktrack.model.TwoLayerPolyrhythmId
import com.vsevolodganin.clicktrack.sounds.UserSelectedSounds
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundType
import com.vsevolodganin.clicktrack.sounds.model.ClickSounds
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundsId
import com.vsevolodganin.clicktrack.storage.ClickSoundsRepository
import com.vsevolodganin.clicktrack.utils.collection.sequence.prefetch
import com.vsevolodganin.clicktrack.utils.collection.toRoundRobin
import com.vsevolodganin.clicktrack.utils.coroutine.delayTillDeadlineUsingCoroutines
import com.vsevolodganin.clicktrack.utils.coroutine.delayTillDeadlineUsingSuspendAndSpinLock
import com.vsevolodganin.clicktrack.utils.coroutine.delayTillDeadlineUsingThreadSleep
import com.vsevolodganin.clicktrack.utils.coroutine.delayTillDeadlineUsingThreadSleepAndSpinLock
import com.vsevolodganin.clicktrack.utils.grabIf
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

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
    private val userSelectedSounds: UserSelectedSounds,
) : Player {

    private var playerJob: Job? = null
    private val playbackState = MutableStateFlow<InternalPlaybackState?>(null)
    private val pausedState = MutableStateFlow(false)
    private val latencyState = MutableStateFlow(Duration.ZERO)

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
                reportLatency = latencyState::tryEmit,
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
                reportLatency = latencyState::tryEmit,
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

    override fun playbackState(): Flow<PlaybackState?> = externalPlaybackState

    private val externalPlaybackState = combine(
        playbackState,
        latencyState
            .map { Const.LATENCY_RESOLUTION * (it / Const.LATENCY_RESOLUTION).toInt() }
            .distinctUntilChanged()
    ) { internalPlaybackState, latency -> internalPlaybackState to latency }
        .mapLatest { (internalPlaybackState, latency) ->
            internalPlaybackState ?: return@mapLatest null

            val emissionTime = internalPlaybackState.creationTime + latency

            // If sound will be emitted in the future due to high latency (soundEmissionTime.elapsedNow() < 0)
            // then wait for it to actually happen
            if (emissionTime.elapsedNow().isNegative()) {
                delay(emissionTime.elapsedNow().absoluteValue)
            }

            PlaybackState(
                id = internalPlaybackState.id,
                progress = PlayProgress(
                    position = internalPlaybackState.progress,
                    emissionTime = emissionTime,
                ),
            )
        }
        .shareIn(GlobalScope, SharingStarted.Eagerly, 1)

    private suspend fun ClickTrack.play(
        startAt: Duration,
        reportProgress: (Duration) -> Unit,
        reportLatency: (Duration) -> Unit,
        soundsSelector: () -> ClickSounds?,
    ) {
        @Suppress("NAME_SHADOWING") // Because that looks better
        val startAt = if (startAt >= durationInTime) {
            if (loop) Duration.ZERO else return
        } else {
            startAt
        }

        val schedule = toPlayerEvents()
            .toActions(
                waitResume = {
                    if (pausedState.value) {
                        pausedState.filter { false }.take(1).collect()
                    }
                },
                selectedSounds = soundsSelector,
                reportLatency = reportLatency,
                soundPool = soundPool,
            )
            .withSideEffect(atIndex = 0) {
                reportProgress(Duration.ZERO)
            }
            .loop(loop)
            .let {
                if (startAt > Duration.ZERO) {
                    it.startingAt(startAt).withSideEffect(atIndex = 0) { reportProgress(startAt) }
                } else {
                    it
                }
            }
            .prefetch(Const.PREFETCH_SIZE)

        PlayerSequencer.play(schedule)
    }

    private suspend fun TwoLayerPolyrhythm.play(
        startAt: Duration,
        reportProgress: (Duration) -> Unit,
        reportLatency: (Duration) -> Unit,
        soundsSelector: () -> ClickSounds?,
    ) {
        @Suppress("NAME_SHADOWING") // Because that looks better
        val startAt = if (startAt >= durationInTime) {
            Duration.ZERO
        } else {
            startAt
        }

        val schedule = toPlayerEvents()
            .toActions(
                waitResume = {
                    if (pausedState.value) {
                        pausedState.filter { false }.take(1).collect()
                    }
                },
                selectedSounds = soundsSelector,
                reportLatency = reportLatency,
                soundPool = soundPool,
            )
            .withSideEffect(atIndex = 0) { reportProgress(Duration.ZERO) }
            .toList()
            .asSequence()
            .loop(true)
            .let {
                if (startAt > Duration.ZERO) {
                    it.startingAt(startAt).withSideEffect(atIndex = 0) { reportProgress(startAt) }
                } else {
                    it
                }
            }
            .prefetch(Const.PREFETCH_SIZE)

        PlayerSequencer.play(schedule)
    }

    private suspend fun CoroutineScope.soundsSelector(soundsId: ClickSoundsId?): () -> ClickSounds? {
        return (if (soundsId != null) soundsById(soundsId) else userSelectedSounds.get())
            .onEach { sounds ->
                sounds?.asIterable?.forEach(soundPool::warmup)
            }
            .stateIn(scope = this)::value
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
        val creationTime = PlayableProgressTimeSource.markNow()

        fun calculateCurrentProgress(): Double {
            val elapsedSinceCreation = creationTime.elapsedNow()
            return (progress + elapsedSinceCreation) / duration
        }
    }

    private object Const {
        const val PREFETCH_SIZE = 100

        // Higher means lower precision when correcting UI for latency
        // Lower means higher precision but more progress bar jumps due to more frequent updates
        val LATENCY_RESOLUTION = 50.milliseconds
    }
}

private class PlayerAction(
    val interval: Duration,
    val action: suspend () -> Unit,
)

private fun Sequence<PlayerEvent>.toActions(
    waitResume: suspend () -> Unit,
    selectedSounds: () -> ClickSounds?,
    reportLatency: (Duration) -> Unit,
    soundPool: PlayerSoundPool,
) = map {
    it.toAction(
        waitResume = waitResume,
        selectedSounds = selectedSounds,
        reportLatency = reportLatency,
        soundPool = soundPool,
    )
}

private fun PlayerEvent.toAction(
    waitResume: suspend () -> Unit,
    selectedSounds: () -> ClickSounds?,
    reportLatency: (Duration) -> Unit,
    soundPool: PlayerSoundPool,
) = PlayerAction(
    interval = duration,
    action = {
        waitResume()

        val soundsSources = selectedSounds()
        if (soundsSources != null) {
            this.sounds.forEach { sound ->
                val soundSource = when (sound) {
                    ClickSoundType.STRONG -> soundsSources.strongBeat
                    ClickSoundType.WEAK -> soundsSources.weakBeat
                }

                soundSource?.let(soundPool::play)

                reportLatency(soundSource?.let(soundPool::latency) ?: Duration.ZERO)
            }
        }
    }
)

private fun Sequence<PlayerAction>.withSideEffect(
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

private fun Sequence<PlayerAction>.startingAt(startAt: Duration): Sequence<PlayerAction> {
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

private fun Sequence<PlayerAction>.loop(loop: Boolean): Sequence<PlayerAction> {
    return if (loop) toRoundRobin() else this
}

private object PlayerSequencer {

    enum class DelayMethod {
        THREAD_SLEEP, SUSPEND, THREAD_SLEEP_SPIN_LOCK, SUSPEND_SPIN_LOCK
    }

    suspend fun play(
        schedule: Sequence<PlayerAction>,
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
