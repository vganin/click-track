package com.vsevolodganin.clicktrack.utils.android.media

import android.media.SoundPool
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive

class SoundPoolSuspendingLoadWait(soundPool: SoundPool) {

    private val loadCompleteEventChannel = Channel<LoadCompleteEvent>()

    init {
        soundPool.setOnLoadCompleteListener { _, id, _ ->
            loadCompleteEventChannel.offer(LoadCompleteEvent(id))
        }
    }

    suspend operator fun invoke(id: Int): Int = coroutineScope {
        val resultAsync = async(start = CoroutineStart.UNDISPATCHED) {
            var loadEvent: LoadCompleteEvent
            do {
                loadEvent = loadCompleteEventChannel.receive()
            } while (isActive && loadEvent.id != id)
            loadEvent
        }
        resultAsync.await().id
    }

    private class LoadCompleteEvent(val id: Int)
}
