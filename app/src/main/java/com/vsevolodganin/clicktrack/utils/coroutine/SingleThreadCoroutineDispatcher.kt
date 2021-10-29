package com.vsevolodganin.clicktrack.utils.coroutine

import android.os.Process
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher

fun createSingleThreadCoroutineDispatcher(
    linuxThreadName: String,
    linuxThreadPriority: Int,
): CoroutineDispatcher {
    return Executors.newSingleThreadExecutor { runnable -> Thread(runnable, linuxThreadName) }
        .also { it.setThreadPriority(linuxThreadPriority) }
        .asCoroutineDispatcher()
}

private fun ExecutorService.setThreadPriority(threadPriority: Int) {
    execute {
        Process.setThreadPriority(threadPriority)
    }
}
