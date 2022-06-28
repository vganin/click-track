package com.vsevolodganin.clicktrack.common

import android.os.SystemClock
import com.vsevolodganin.clicktrack.model.BeatsPerMinute
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class BpmMeter @Inject constructor() {

    private val samples: Array<Long?> = Array(HISTORY_SIZE) { null }
    private var index: Int = 0

    fun addTap() {
        val now = now()
        samples[index] = now
        index = (index + 1) % HISTORY_SIZE
    }

    fun calculateBpm(): BeatsPerMinute? {
        val now = now()
        val applicableSamples = samples
            .asSequence()
            .filterNotNull()
            .filter { now - it < TIME_WINDOW_SIZE_MILLIS }
            .toList()
            .takeIf { it.size >= MIN_COUNT_TO_DERIVE_BPM }
            ?: return null
        val minTime = applicableSamples.minOrNull() ?: return null
        val maxTime = applicableSamples.maxOrNull() ?: return null
        return BeatsPerMinute(
            beatCount = applicableSamples.size - 1,
            timelapse = (maxTime - minTime).milliseconds,
        )
    }

    private fun now() = SystemClock.uptimeMillis()
}

private const val TIME_WINDOW_SIZE_MILLIS = 3000L
private const val HISTORY_SIZE = 10
private const val MIN_COUNT_TO_DERIVE_BPM = 3
