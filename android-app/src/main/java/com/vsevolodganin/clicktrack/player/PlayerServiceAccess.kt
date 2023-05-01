package com.vsevolodganin.clicktrack.player

import android.app.Activity
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.vsevolodganin.clicktrack.di.component.ActivityScope
import com.vsevolodganin.clicktrack.model.ClickSoundsId
import com.vsevolodganin.clicktrack.model.PlayableId
import com.vsevolodganin.clicktrack.utils.decompose.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import me.tatarka.inject.annotations.Inject

@ActivityScope
@Inject
class PlayerServiceAccess(
    private val activity: Activity,
    lifecycleOwner: LifecycleOwner
) {
    private val scope = lifecycleOwner.coroutineScope()

    private val binderState = MutableStateFlow<PlayerServiceBinder?>(null)

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            binderState.value = service as PlayerServiceBinder
        }

        override fun onServiceDisconnected(name: ComponentName) {
            binderState.value = null
        }
    }

    private val playbackState = binderState.flatMapLatest { it?.playbackState ?: flowOf(null) }
        .onStart {
            PlayerService.bind(activity, serviceConnection)
        }
        .onCompletion {
            activity.unbindService(serviceConnection)
        }
        .shareIn(scope, SharingStarted.Eagerly, replay = 1)

    fun start(
        id: PlayableId,
        atProgress: Double? = null,
        soundsId: ClickSoundsId? = null
    ) = PlayerService.start(activity, id, atProgress, soundsId)

    fun pause() = PlayerService.pause(activity)

    fun resume() = PlayerService.resume(activity)

    fun stop() = PlayerService.stop(activity)

    fun playbackState(): Flow<PlaybackState?> = playbackState
}
