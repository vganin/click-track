package com.vsevolodganin.clicktrack.player

import com.vsevolodganin.clicktrack.di.component.PlayerServiceScope
import com.vsevolodganin.clicktrack.di.module.PlayerDispatcher
import com.vsevolodganin.clicktrack.model.ClickSounds
import com.vsevolodganin.clicktrack.model.ClickSoundsId
import com.vsevolodganin.clicktrack.model.ClickTrack
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.PlayProgress
import com.vsevolodganin.clicktrack.model.PlayableId
import com.vsevolodganin.clicktrack.model.PlayableProgressTimeMark
import com.vsevolodganin.clicktrack.model.PlayableProgressTimeSource
import com.vsevolodganin.clicktrack.model.TwoLayerPolyrhythm
import com.vsevolodganin.clicktrack.model.TwoLayerPolyrhythmId
import com.vsevolodganin.clicktrack.primitiveaudio.PrimitiveAudioPlayer
import com.vsevolodganin.clicktrack.soundlibrary.SoundSourceProvider
import com.vsevolodganin.clicktrack.soundlibrary.UserSelectedSounds
import com.vsevolodganin.clicktrack.storage.ClickSoundsRepository
import com.vsevolodganin.clicktrack.utils.grabIf
import com.vsevolodganin.clicktrack.utils.log.Logger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.withIndex
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@PlayerServiceScope
@Inject
class Player(
    private val playerDispatcher: PlayerDispatcher,
    private val primitiveAudioPlayer: PrimitiveAudioPlayer,
    private val clickSoundsRepository: ClickSoundsRepository,
    private val playableContentProvider: PlayableContentProvider,
    private val userSelectedSounds: UserSelectedSounds,
    latencyTracker: LatencyTracker,
    private val logger: Logger,
) {
    data class Input(
        val id: PlayableId,
        val startAtProgress: Double?,
        val soundsId: ClickSoundsId?,
    )

    private val playbackState = MutableStateFlow<InternalPlaybackState?>(null)
    private val pausedState = MutableStateFlow(false)

    suspend fun play(input: Flow<Input>) {
        try {
            input.collectLatest { (id, startAtProgress, soundsId) ->
                play(id, startAtProgress, soundsId)
            }
        } finally {
            withContext(NonCancellable) {
                playbackState.value = null
            }
        }
    }

    fun pause() {
        pausedState.value = true
    }

    fun resume() {
        pausedState.value = false
    }

    private suspend fun play(
        id: PlayableId,
        startAtProgress: Double?,
        soundsId: ClickSoundsId?,
    ) {
        when (id) {
            is ClickTrackId -> {
                playableContentProvider.clickTrackFlow(id)
                    .withIndex()
                    .collectLatest inner@{ (index, clickTrack) ->
                        clickTrack ?: return@inner
                        pausable(if (index == 0) startAtProgress else null) { progress ->
                            play(id, clickTrack, progress, soundsId)
                        }
                    }
            }

            TwoLayerPolyrhythmId -> {
                playableContentProvider.twoLayerPolyrhythmFlow()
                    .withIndex()
                    .collectLatest { (index, polyrhythm) ->
                        pausable(if (index == 0) startAtProgress else null) { progress ->
                            play(polyrhythm, progress, soundsId)
                        }
                    }
            }
        }
    }

    private suspend inline fun pausable(
        startAt: Double?,
        crossinline play: suspend (startAt: Double?) -> Unit,
    ) {
        var savedProgress: Double? = startAt
        pausedState.collectLatest { isPaused ->
            if (isPaused) {
                savedProgress = playbackState.value?.realProgress
            } else {
                play(savedProgress)
            }
        }
    }

    private suspend fun play(
        id: ClickTrackId,
        clickTrack: ClickTrack,
        atProgress: Double?,
        soundsId: ClickSoundsId?,
    ) = withContext(playerDispatcher) {
        val duration = clickTrack.durationInTime

        if (duration == Duration.ZERO) {
            logger.logError(TAG, "Tried to play track with zero duration, exiting")
            return@withContext
        }

        val currentPlayback = playbackState.value
        val progress = atProgress
            ?: grabIf(id == currentPlayback?.id) { currentPlayback?.realProgress }
            ?: 0.0
        val startAt = duration * progress

        clickTrack.play(
            startAt = startAt,
            reportProgress = {
                playbackState.value = InternalPlaybackState(
                    id = id,
                    duration = duration,
                    position = it,
                )
            },
            soundSourceProvider = soundSourceProvider(soundsId),
        )
    }

    private suspend fun play(
        twoLayerPolyrhythm: TwoLayerPolyrhythm,
        atProgress: Double?,
        soundsId: ClickSoundsId?,
    ) = withContext(playerDispatcher) {
        val duration = twoLayerPolyrhythm.durationInTime

        if (duration == Duration.ZERO) {
            logger.logError(TAG, "Tried to play polyrhythm with zero duration, exiting")
            return@withContext
        }

        val currentPlayback = playbackState.value
        val progress = atProgress
            ?: grabIf(TwoLayerPolyrhythmId == currentPlayback?.id) { currentPlayback?.realProgress }
            ?: 0.0
        val startAt = duration * progress

        twoLayerPolyrhythm.play(
            startAt = startAt,
            reportProgress = {
                playbackState.value = InternalPlaybackState(
                    id = TwoLayerPolyrhythmId,
                    duration = duration,
                    position = it,
                )
            },
            soundSourceProvider = soundSourceProvider(soundsId),
        )
    }

    fun playbackState(): Flow<PlaybackState?> = externalPlaybackState

    private val externalPlaybackState = combine(
        playbackState,
        pausedState,
        latencyTracker.latencyState
            .map { LATENCY_RESOLUTION * (it / LATENCY_RESOLUTION).toInt() }
            .distinctUntilChanged(),
    ) { internalPlaybackState, isPaused, latency -> Triple(internalPlaybackState, isPaused, latency) }
        .mapLatest { (internalPlaybackState, isPaused, latency) ->
            if (!isPaused) {
                delay(latency)
            }

            internalPlaybackState ?: return@mapLatest null

            PlaybackState(
                id = internalPlaybackState.id,
                progress = PlayProgress(
                    position = internalPlaybackState.realPosition - latency,
                    isPaused = isPaused,
                ),
            )
        }
        .shareIn(GlobalScope, SharingStarted.Eagerly, 1)

    private suspend fun ClickTrack.play(
        startAt: Duration,
        reportProgress: (Duration) -> Unit,
        soundSourceProvider: SoundSourceProvider,
    ) {
        val durationInTime = durationInTime
        val startingAt = if (startAt >= durationInTime) {
            if (loop) Duration.ZERO else return
        } else {
            startAt
        }

        primitiveAudioPlayer.play(
            startingAt = startingAt,
            singleIterationDuration = durationInTime,
            playerEvents = toPlayerEvents()
                .loop(loop)
                .startingAt(startingAt),
            reportProgress = reportProgress,
            soundSourceProvider = soundSourceProvider,
        )
    }

    private suspend fun TwoLayerPolyrhythm.play(
        startAt: Duration,
        reportProgress: (Duration) -> Unit,
        soundSourceProvider: SoundSourceProvider,
    ) {
        val durationInTime = durationInTime
        val startingAt = if (startAt >= durationInTime) {
            Duration.ZERO
        } else {
            startAt
        }

        primitiveAudioPlayer.play(
            startingAt = startingAt,
            singleIterationDuration = durationInTime,
            playerEvents = toPlayerEvents()
                .loop(true)
                .startingAt(startingAt),
            reportProgress = reportProgress,
            soundSourceProvider = soundSourceProvider,
        )
    }

    private suspend fun soundSourceProvider(soundsId: ClickSoundsId?): SoundSourceProvider {
        val soundsFlow = if (soundsId != null) soundsById(soundsId) else userSelectedSounds.get()
        val soundsState = soundsFlow.stateIn(GlobalScope)
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
        val position: Duration,
    ) {
        private val emissionTime: PlayableProgressTimeMark = PlayableProgressTimeSource.markNow()

        val realPosition: Duration get() = position + emissionTime.elapsedNow()

        val realProgress: Double get() = realPosition / duration
    }

    private companion object Const {
        const val TAG = "Player"

        // Higher means lower precision when correcting UI for latency
        // Lower means higher precision but more progress bar jumps due to more frequent updates
        val LATENCY_RESOLUTION = 50.milliseconds
    }
}
