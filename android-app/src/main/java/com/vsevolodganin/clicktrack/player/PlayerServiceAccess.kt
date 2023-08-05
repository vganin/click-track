package com.vsevolodganin.clicktrack.player

import android.app.Activity
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.subscribe
import com.vsevolodganin.clicktrack.di.component.MainControllerScope
import com.vsevolodganin.clicktrack.model.ClickSoundsId
import com.vsevolodganin.clicktrack.model.PlayableId
import com.vsevolodganin.clicktrack.utils.decompose.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@MainControllerScope
@Inject
class PlayerServiceAccess(
    private val activity: Activity,
    lifecycleOwner: LifecycleOwner
) : Player {

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

    init {
        lifecycleOwner.lifecycle.subscribe(
            onCreate = {
                PlayerService.bind(activity, serviceConnection)
            },
            onDestroy = {
                activity.unbindService(serviceConnection)
            }
        )
    }

    override val playbackState = binderState.flatMapLatest { it?.player?.playbackState ?: flowOf(null) }

    override fun setPlayable(id: PlayableId) = onBoundPlayer { setPlayable(id) }

    override fun setSounds(id: ClickSoundsId?) = onBoundPlayer { setSounds(id) }

    override fun setProgress(progress: Double) = onBoundPlayer { setProgress(progress) }

    override fun play() = onBoundPlayer(Player::play)

    override fun pause() = onBoundPlayer(Player::pause)

    override fun stop() = onBoundPlayer(Player::stop)

    private fun onBoundPlayer(action: Player.() -> Unit) {
        scope.launch {
            binderState.filterNotNull().first().player.action()
        }
    }
}
