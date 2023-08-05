package com.vsevolodganin.clicktrack.player

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.subscribe
import com.vsevolodganin.clicktrack.audio.AudioSink
import com.vsevolodganin.clicktrack.audio.PcmResampler
import com.vsevolodganin.clicktrack.audio.SoundBank
import com.vsevolodganin.clicktrack.audio.SoundSourceProvider
import com.vsevolodganin.clicktrack.audio.UserSelectedSounds
import com.vsevolodganin.clicktrack.di.component.PlayerServiceScope
import com.vsevolodganin.clicktrack.model.ClickSounds
import com.vsevolodganin.clicktrack.model.ClickSoundsId
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.PlayableId
import com.vsevolodganin.clicktrack.model.TwoLayerPolyrhythmId
import com.vsevolodganin.clicktrack.storage.ClickSoundsRepository
import com.vsevolodganin.clicktrack.utils.collection.toRoundRobin
import com.vsevolodganin.clicktrack.utils.media.AudioFormatHelper
import com.vsevolodganin.clicktrack.utils.optionalCast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import kotlin.time.Duration
import kotlin.time.DurationUnit

@PlayerServiceScope
@Inject
class PlayerCore(
    lifecycle: Lifecycle,
    private val clickSoundsRepository: ClickSoundsRepository,
    private val playableContentProvider: PlayableContentProvider,
    private val userSelectedSounds: UserSelectedSounds,
    private val soundBank: SoundBank,
    private val pcmResampler: PcmResampler,
    private val audioSink: AudioSink
) : Player {
    private val playerState = MutableStateFlow<PlayerState?>(null)
    private val soundsState = MutableStateFlow<ClickSoundsId?>(null)
    private val setProgressRequests = Channel<Double>(
        capacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val playbackPosition = MutableStateFlow(PlaybackPosition(Duration.ZERO))

    init {
        var playerStateApplicationJob: Job? = null
        lifecycle.subscribe(
            onCreate = {
                playerStateApplicationJob = GlobalScope.launch(Dispatchers.Main) {
                    applyPlayerState()
                }
            },
            onDestroy = {
                playerStateApplicationJob?.cancel()
            }
        )
    }

    override val playbackState: StateFlow<PlaybackState?>
        get() {
            return combine(
                playerState,
                playbackPosition
            ) { playerState, playbackPosition ->
                playerState ?: return@combine null
                PlaybackState(
                    id = playerState.id,
                    isPaused = playerState.isPaused,
                    position = playbackPosition
                )
            }.stateIn(GlobalScope, SharingStarted.Eagerly, null)
        }

    override fun setPlayable(id: PlayableId) {
        playerState.update { state ->
            if (state?.id != id) {
                PlayerState(
                    id = id,
                    isPaused = state?.isPaused ?: true,
                )
            } else {
                state
            }
        }
    }

    override fun setSounds(id: ClickSoundsId?) {
        soundsState.value = id
    }

    override fun setProgress(progress: Double) {
        setProgressRequests.trySend(progress)
    }

    override fun play() {
        playerState.update { state ->
            state?.copy(isPaused = false)
        }
    }

    override fun stop() {
        playerState.value = null
    }

    override fun pause() {
        playerState.update { state ->
            state?.copy(isPaused = true)
        }
    }

    private suspend fun applyPlayerState() = coroutineScope {
        launch {
            playerState.map { it?.id }.distinctUntilChanged().collectLatest { id ->
                when (id) {
                    is ClickTrackId -> {
                        playableContentProvider.clickTrackFlow(id).collectLatest inner@{ clickTrack ->
                            clickTrack ?: return@inner
                            prepareToPlay(
                                overrideSoundsId = id.optionalCast<ClickTrackId.Builtin.ClickSoundsTest>()?.soundsId,
                                playerEvents = clickTrack.toPlayerEvents(),
                                duration = clickTrack.durationInTime,
                                loop = clickTrack.loop,
                            )
                        }
                    }
                    TwoLayerPolyrhythmId -> {
                        playableContentProvider.twoLayerPolyrhythmFlow().collectLatest { polyrhythm ->
                            prepareToPlay(
                                overrideSoundsId = null,
                                playerEvents = polyrhythm.toPlayerEvents(),
                                duration = polyrhythm.durationInTime,
                                loop = true,
                            )
                        }
                    }
                    null -> Unit
                }
            }
        }
        launch {
            playerState.map { it?.isPaused }.distinctUntilChanged().collectLatest { isPaused ->
                when (isPaused) {
                    true -> audioSink.pause()
                    false -> audioSink.play()
                    null -> audioSink.stop()
                }
            }
        }
    }

    private suspend fun prepareToPlay(
        overrideSoundsId: ClickSoundsId?,
        playerEvents: Sequence<PlayerEvent>,
        duration: Duration,
        loop: Boolean,
    ) {
        val soundsSourceProvider = soundSourceProvider(overrideSoundsId)
        val initialProgress = setProgressRequests.tryReceive().getOrNull() ?: 0.0
        val setProgressRequestsFlow = setProgressRequests.consumeAsFlow().onStart { emit(initialProgress) }

        setProgressRequestsFlow.collectLatest { progress ->
            prepareToPlay(
                playerEvents = playerEvents,
                duration = duration,
                loop = loop,
                startAtProgress = progress,
                soundsSourceProvider = soundsSourceProvider
            )
        }
    }

    private suspend fun prepareToPlay(
        playerEvents: Sequence<PlayerEvent>,
        duration: Duration,
        loop: Boolean,
        startAtProgress: Double,
        soundsSourceProvider: SoundSourceProvider,
    ) {
        val sampleRate = AudioSink.SAMPLE_RATE
        val channelCount = AudioFormatHelper.channelMaskToChannelCount(AudioSink.CHANNEL_MASK)
        val bitDepth = AudioFormatHelper.pcmEncodingToBitDepth(AudioSink.PCM_ENCODING)
        val bytesPerSample = AudioFormatHelper.bytesPerSample(bitDepth)
        val bytesPerFrame = AudioFormatHelper.bytesPerFrame(bytesPerSample, channelCount)

        val startAtDuration = duration * startAtProgress
        val startAtSeconds = startAtDuration.toDouble(DurationUnit.SECONDS)
        val startIndex = (startAtSeconds * sampleRate * bytesPerSample / bytesPerFrame).toInt() * bytesPerFrame

        val bufferSize = BUFFER_LENGTH_SECONDS * sampleRate * bytesPerSample

        val iterationBytes = playerEvents.toBytes(
            bitDepth = bitDepth,
            sampleRate = sampleRate,
            resampler = pcmResampler,
            soundSourceProvider = soundsSourceProvider,
            soundBank = soundBank,
        )

        val firstIteration = iterationBytes.drop(startIndex)

        val iterations = sequenceOf(firstIteration) + if (loop) {
            sequenceOf(iterationBytes).toRoundRobin()
        } else {
            emptySequence()
        }

        playbackPosition.value = PlaybackPosition(startAtDuration)

        for (iteration in iterations) {
            val chunkedIteration = iteration.chunked(bufferSize) { bytes ->
                ByteArray(bytes.size) { index -> bytes[index] }
            }

            for (chunk in chunkedIteration) {
                audioSink.write(chunk)
            }

            playbackPosition.value = PlaybackPosition(Duration.ZERO)
        }

        stop()
    }

    private suspend fun soundSourceProvider(overrideSoundsId: ClickSoundsId?): SoundSourceProvider {
        return if (overrideSoundsId != null) {
            soundsById(overrideSoundsId)
        } else {
            soundsState.flatMapLatest { soundsId ->
                if (soundsId != null) soundsById(soundsId) else userSelectedSounds.get()
            }
        }.stateIn(GlobalScope).let(::SoundSourceProvider)
    }

    private fun soundsById(soundsId: ClickSoundsId): Flow<ClickSounds?> {
        return when (soundsId) {
            is ClickSoundsId.Builtin -> flowOf(soundsId.value.sounds)
            is ClickSoundsId.Database -> clickSoundsRepository.getById(soundsId).map { it?.value }
        }
    }

    private data class PlayerState(
        val id: PlayableId,
        val isPaused: Boolean
    )

    private companion object {
        const val BUFFER_LENGTH_SECONDS = 5
    }
}
