package com.vsevolodganin.clicktrack.player

import clicktrack.multiplatform.generated.resources.Res
import clicktrack.multiplatform.generated.resources.player_service_notification_playing_now
import clicktrack.multiplatform.generated.resources.player_service_notification_polyrhythm_title
import com.vsevolodganin.clicktrack.di.component.PlayerServiceScope
import com.vsevolodganin.clicktrack.model.ClickSoundsId
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.PlayableId
import com.vsevolodganin.clicktrack.model.TwoLayerPolyrhythmId
import com.vsevolodganin.clicktrack.utils.MultiplatformRes
import com.vsevolodganin.clicktrack.utils.string
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.getString
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.AVAudioSessionModeDefault
import platform.AVFAudio.setActive

@OptIn(ExperimentalForeignApi::class)
@SingleIn(PlayerServiceScope::class)
@ContributesBinding(PlayerServiceScope::class)
@Inject
class PlayerServiceAccessImpl(
    private val scope: CoroutineScope,
    private val player: Player,
    latencyTracker: LatencyTracker,
    private val playableContentProvider: PlayableContentProvider,
    private val audioSessionNotification: AudioSessionNotification,
) : PlayerServiceAccess {

    @Serializable
    private data class State(
        val id: PlayableId,
        val startAtProgress: Double?,
        val soundsId: ClickSoundsId?,
        val isPaused: Boolean,
    )

    private val state = MutableStateFlow<State?>(null)

    private val audioSession = AVAudioSession.sharedInstance()

    init {
        with(audioSession) {
            setCategory(AVAudioSessionCategoryPlayback, error = null)
            setMode(AVAudioSessionModeDefault, error = null)
        }
        audioSessionNotification.setCallbacks(
            onPause = ::pause,
            onResume = ::resume,
            onStop = ::stop,
        )
        initializePlayer()
        latencyTracker.start()
    }

    override fun start(
        id: PlayableId,
        atProgress: Double?,
        soundsId: ClickSoundsId?,
    ) {
        state.value = State(id, atProgress, soundsId, isPaused = false)
    }

    override fun pause() {
        state.update { it?.copy(isPaused = true) }
    }

    override fun resume() {
        state.update { it?.copy(isPaused = false) }
    }

    override fun stop() {
        state.value = null
    }

    override fun playbackState(): Flow<PlaybackState?> {
        return player.playbackState()
    }

    private fun initializePlayer() {
        scope.apply {
            launch { playbackComponent() }
            launch { audioSessionComponent() }
        }
    }

    private suspend fun playbackComponent() = coroutineScope {
        launch {
            state.map { it?.isPaused }.distinctUntilChanged().collectLatest { isPaused ->
                when (isPaused) {
                    true -> player.pause()
                    false -> player.resume()
                    null -> Unit
                }
            }
        }

        launch {
            fun State.toPlayerInput() = Player.Input(id, startAtProgress, soundsId)

            state.map { it != null }.distinctUntilChanged().collectLatest { startPlay ->
                if (startPlay) {
                    player.play(
                        state
                            .filterNotNull()
                            .map(State::toPlayerInput)
                            .distinctUntilChanged(),
                    )
                }

                state.emit(null)
            }
        }
    }

    private suspend fun audioSessionComponent() {
        state.collectLatest { args ->
            if (args != null) {
                when (val id = args.id) {
                    is ClickTrackId -> {
                        playableContentProvider.clickTrackFlow(id)
                            .filterNotNull()
                            .map { it.name }
                            .distinctUntilChanged()
                            .collectLatest { name ->
                                activateAudioSession(
                                    contentText = name,
                                    isPaused = args.isPaused,
                                )
                            }
                    }

                    TwoLayerPolyrhythmId -> {
                        playableContentProvider.twoLayerPolyrhythmFlow()
                            .collectLatest { polyrhythm ->
                                activateAudioSession(
                                    contentText = getString(
                                        MultiplatformRes.string.player_service_notification_polyrhythm_title,
                                        polyrhythm.layer1,
                                        polyrhythm.layer2,
                                    ),
                                    isPaused = args.isPaused,
                                )
                            }
                    }
                }
            } else {
                deactivateAudioSession()
            }
        }
    }

    private suspend fun activateAudioSession(contentText: String, isPaused: Boolean) {
        audioSession.setActive(true, null)
        audioSessionNotification.show(
            title = getString(Res.string.player_service_notification_playing_now),
            contentText = contentText,
            isPaused = isPaused,
        )
    }

    private fun deactivateAudioSession() {
        audioSession.setActive(false, null)
        audioSessionNotification.hide()
    }
}
