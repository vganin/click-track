package com.vsevolodganin.clicktrack.meter

import android.os.SystemClock
import com.vsevolodganin.clicktrack.lib.BeatsPerMinute
import javax.inject.Inject
import kotlin.time.Duration

class BpmMeter @Inject constructor() {

    private val lock = Any()
    private val samples: Array<Long?> = Array(HISTORY_SIZE) { null }
    private var index: Int = 0

    fun addTap() = synchronized(lock) {
        val now = now()
        index = (index + 1) % HISTORY_SIZE
        samples[index] = now
    }

    fun calculateBpm(): BeatsPerMinute? = synchronized(lock) {
        val now = now()
        val applicableSamples = samples
            .asSequence()
            .filterNotNull()
            .filter { now - it < TIME_WINDOW_SIZE }
            .toList()
            .takeIf { it.size >= MIN_COUNT_TO_DERIVE_BPM }
            ?: return null
        val minTime = applicableSamples.minOrNull() ?: return null
        val maxTime = applicableSamples.maxOrNull() ?: return null
        return BeatsPerMinute(
            beatCount = applicableSamples.size,
            timelapse = Duration.milliseconds((maxTime - minTime)),
        )
    }

    private fun now() = SystemClock.uptimeMillis()
}

private const val TIME_WINDOW_SIZE = 3000L // ms
private const val HISTORY_SIZE = 6
private const val MIN_COUNT_TO_DERIVE_BPM = 2
