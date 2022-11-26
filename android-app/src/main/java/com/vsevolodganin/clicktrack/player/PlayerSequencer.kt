package com.vsevolodganin.clicktrack.player

import android.os.SystemClock
import com.vsevolodganin.clicktrack.utils.coroutine.delayTillDeadlineUsingCoroutines
import com.vsevolodganin.clicktrack.utils.coroutine.delayTillDeadlineUsingSuspendAndSpinLock
import com.vsevolodganin.clicktrack.utils.coroutine.delayTillDeadlineUsingThreadSleep
import com.vsevolodganin.clicktrack.utils.coroutine.delayTillDeadlineUsingThreadSleepAndSpinLock
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

object PlayerSequencer {

    enum class DelayMethod {
        THREAD_SLEEP,
        SUSPEND,
        THREAD_SLEEP_SPIN_LOCK,
        SUSPEND_SPIN_LOCK
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
