package com.vsevolodganin.clicktrack.player

import com.vsevolodganin.clicktrack.audio.SoundSourceProvider
import com.vsevolodganin.clicktrack.audio.UserSelectedSounds
import com.vsevolodganin.clicktrack.di.component.PlayerServiceScope
import com.vsevolodganin.clicktrack.di.module.PlayerDispatcher
import com.vsevolodganin.clicktrack.model.ClickSounds
import com.vsevolodganin.clicktrack.model.ClickSoundsId
import com.vsevolodganin.clicktrack.model.ClickTrack
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.PlayProgress
import com.vsevolodganin.clicktrack.model.PlayableId
import com.vsevolodganin.clicktrack.model.PlayableProgressTimeSource
import com.vsevolodganin.clicktrack.model.TwoLayerPolyrhythm
import com.vsevolodganin.clicktrack.model.TwoLayerPolyrhythmId
import com.vsevolodganin.clicktrack.storage.ClickSoundsRepository
import com.vsevolodganin.clicktrack.utils.collection.sequence.prefetch
import com.vsevolodganin.clicktrack.utils.coroutine.collectLatestFirst
import com.vsevolodganin.clicktrack.utils.grabIf
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
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
import kotlinx.coroutines.flow.withIndex
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@PlayerServiceScope
class Player @Inject constructor(
    @PlayerDispatcher private val playerDispatcher: CoroutineDispatcher,
    private val soundPool: PlayerSoundPool,
    private val clickSoundsRepository: ClickSoundsRepository,
    private val playableContentProvider: PlayableContentProvider,
    private val userSelectedSounds: UserSelectedSounds,
) {
    private val playbackState = MutableStateFlow<InternalPlaybackState?>(null)
    private val pausedState = MutableStateFlow(false)
    private val latencyState = MutableStateFlow(Duration.ZERO)

    suspend fun play(
        id: PlayableId,
        startAtProgress: Double?,
        soundsId: ClickSoundsId?,
    ) {
        try {
            when (id) {
                is ClickTrackId -> {
                    playableContentProvider.clickTrackFlow(id)
                        .withIndex()
                        .collectLatestFirst { (index, clickTrack) ->
                            clickTrack ?: return@collectLatestFirst
                            val atProgress = if (index == 0) startAtProgress else null
                            play(id, clickTrack, atProgress, soundsId)
                        }
                }
                TwoLayerPolyrhythmId -> {
                    playableContentProvider.twoLayerPolyrhythmFlow()
                        .withIndex()
                        .collectLatestFirst { (index, polyrhythm) ->
                            val atProgress = if (index == 0) startAtProgress else null
                            play(polyrhythm, atProgress, soundsId)
                        }
                }
            }
        } finally {
            playbackState.value = null

            // Delaying superfluous sound pool stops in order to avoid
            // race condition on some Android versions which causes
            // audio stream to shut down without any notification.
            // For more info see https://github.com/google/oboe/issues/1315
            delay(500)
            soundPool.stopAll()
        }
    }

    fun pause() {
        pausedState.value = true
    }

    private suspend fun play(
        id: ClickTrackId,
        clickTrack: ClickTrack,
        atProgress: Double?,
        soundsId: ClickSoundsId?,
    ) = withContext(playerDispatcher) {
        val duration = clickTrack.durationInTime

        if (duration == Duration.ZERO) {
            Timber.w("Tried to play track with zero duration, exiting")
            return@withContext
        }

        val currentPlayback = playbackState.value
        val progress = atProgress
            ?: grabIf(id == currentPlayback?.id) { currentPlayback?.calculateCurrentProgress() }
            ?: 0.0
        val startAt = duration * progress

        pausedState.value = false

        clickTrack.play(
            startAt = startAt,
            reportProgress = {
                playbackState.value = InternalPlaybackState(
                    id = id,
                    duration = duration,
                    progress = it,
                )
            },
            reportLatency = latencyState::tryEmit,
            soundSourceProvider = soundSourceProvider(soundsId)
        )
    }

    private suspend fun play(
        twoLayerPolyrhythm: TwoLayerPolyrhythm,
        atProgress: Double?,
        soundsId: ClickSoundsId?,
    ) = withContext(playerDispatcher) {
        val duration = twoLayerPolyrhythm.durationInTime

        if (duration == Duration.ZERO) {
            Timber.w("Tried to play polyrhythm with zero duration, exiting")
            return@withContext
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
            soundSourceProvider = soundSourceProvider(soundsId)
        )
    }

    fun playbackState(): Flow<PlaybackState?> = externalPlaybackState

    private val externalPlaybackState = combine(
        playbackState,
        latencyState
            .map { LATENCY_RESOLUTION * (it / LATENCY_RESOLUTION).toInt() }
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
        soundSourceProvider: SoundSourceProvider,
    ) {
        val actualStartAt = if (startAt >= durationInTime) {
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
                soundSourceProvider = soundSourceProvider,
                reportLatency = reportLatency,
                soundPool = soundPool,
            )
            .withSideEffect(atIndex = 0) {
                reportProgress(Duration.ZERO)
            }
            .loop(loop)
            .let {
                if (actualStartAt > Duration.ZERO) {
                    it.startingAt(actualStartAt).withSideEffect(atIndex = 0) { reportProgress(actualStartAt) }
                } else {
                    it
                }
            }
            .prefetch(PREFETCH_SIZE)

        PlayerSequencer.play(schedule)
    }

    private suspend fun TwoLayerPolyrhythm.play(
        startAt: Duration,
        reportProgress: (Duration) -> Unit,
        reportLatency: (Duration) -> Unit,
        soundSourceProvider: SoundSourceProvider,
    ) {
        val actualStartAt = if (startAt >= durationInTime) {
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
                soundSourceProvider = soundSourceProvider,
                reportLatency = reportLatency,
                soundPool = soundPool,
            )
            .withSideEffect(atIndex = 0) { reportProgress(Duration.ZERO) }
            .toList()
            .asSequence()
            .loop(true)
            .let {
                if (actualStartAt > Duration.ZERO) {
                    it.startingAt(actualStartAt).withSideEffect(atIndex = 0) { reportProgress(actualStartAt) }
                } else {
                    it
                }
            }
            .prefetch(PREFETCH_SIZE)

        PlayerSequencer.play(schedule)
    }

    private suspend fun soundSourceProvider(soundsId: ClickSoundsId?): SoundSourceProvider {
        val soundsFlow = if (soundsId != null) soundsById(soundsId) else userSelectedSounds.get()
        val soundsState = soundsFlow
            .onEach { sounds -> sounds?.asIterable?.forEach(soundPool::warmup) }
            .stateIn(GlobalScope)
        return SoundSourceProvider(soundsState)
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

    private companion object Const {
        const val PREFETCH_SIZE = 100

        // Higher means lower precision when correcting UI for latency
        // Lower means higher precision but more progress bar jumps due to more frequent updates
        val LATENCY_RESOLUTION = 50.milliseconds
    }
}
