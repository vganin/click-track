package com.vsevolodganin.clicktrack.player

import com.vsevolodganin.clicktrack.di.component.PlayerServiceScope
import com.vsevolodganin.clicktrack.model.ClickSoundsId
import com.vsevolodganin.clicktrack.model.PlayableId
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
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

@SingleIn(PlayerServiceScope::class)
@ContributesBinding(PlayerServiceScope::class)
@Inject
class PlayerServiceAccessImpl(
    val scope: CoroutineScope,
    val player: Player,
    latencyTracker: LatencyTracker,
) : PlayerServiceAccess {

    @Serializable
    private data class State(
        val id: PlayableId,
        val startAtProgress: Double?,
        val soundsId: ClickSoundsId?,
        val isPaused: Boolean,
    )

    private val state = MutableStateFlow<State?>(null)

    init {
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
}
