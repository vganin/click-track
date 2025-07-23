package com.vsevolodganin.clicktrack.player

import com.vsevolodganin.clicktrack.di.component.PlayerServiceScope
import com.vsevolodganin.clicktrack.primitiveaudio.PrimitiveAudioPlayer
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@PlayerServiceScope
@Inject
class LatencyTracker(
    private val primitiveAudioPlayer: PrimitiveAudioPlayer,
) {
    private var latencyMeasureJob: Job? = null

    private val _latencyState = MutableStateFlow(Duration.ZERO)
    val latencyState: StateFlow<Duration> = _latencyState

    @OptIn(DelicateCoroutinesApi::class)
    fun start() {
        latencyMeasureJob?.cancel()
        latencyMeasureJob = GlobalScope.launch(Dispatchers.Main) {
            while (isActive) {
                _latencyState.value = primitiveAudioPlayer.getLatencyMs().milliseconds
                delay(1.seconds)
            }
        }
    }

    fun stop() {
        latencyMeasureJob?.cancel()
    }
}
