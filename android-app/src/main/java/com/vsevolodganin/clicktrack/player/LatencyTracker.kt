package com.vsevolodganin.clicktrack.player

import android.media.AudioFormat
import com.vsevolodganin.clicktrack.audio.AudioTrack
import com.vsevolodganin.clicktrack.di.component.PlayerServiceScope
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
class LatencyTracker() {

    private var latencyMeasureJob: Job? = null
    private val dummyAudioTrack = AudioTrack(ByteArray(0), AudioFormat.ENCODING_PCM_16BIT, 2, 44100)

    private val _latencyState = MutableStateFlow(Duration.ZERO)
    val latencyState: StateFlow<Duration> = _latencyState

    fun start() {
        latencyMeasureJob?.cancel()
        latencyMeasureJob = GlobalScope.launch(Dispatchers.Main) {
            try {
                dummyAudioTrack.play()
                while (isActive) {
                    _latencyState.value = dummyAudioTrack.getLatencyMs().milliseconds
                    delay(1.seconds)
                }
            } finally {
                dummyAudioTrack.stop()
            }
        }
    }

    fun stop() {
        latencyMeasureJob?.cancel()
    }
}
