package net.ganin.vsevolod.clicktrack.player

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import androidx.annotation.MainThread
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import net.ganin.vsevolod.clicktrack.di.component.ViewModelScoped
import net.ganin.vsevolod.clicktrack.di.module.ApplicationContext
import net.ganin.vsevolod.clicktrack.di.module.MainDispatcher
import net.ganin.vsevolod.clicktrack.model.ClickTrackWithId
import net.ganin.vsevolod.clicktrack.state.PlaybackState
import javax.inject.Inject

@ViewModelScoped
class PlayerServiceAccess @Inject constructor(
    @ApplicationContext private val context: Context,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher
) : Player {

    private val binderState = MutableStateFlow<PlayerServiceBinder?>(null)

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            binderState.value = service as PlayerServiceBinder
        }

        override fun onServiceDisconnected(name: ComponentName) {
            binderState.value = null
        }
    }

    @MainThread
    fun connect() {
        PlayerService.bind(context, serviceConnection)
    }

    @MainThread
    fun disconnect() {
        context.unbindService(serviceConnection)
    }

    override suspend fun play(clickTrack: ClickTrackWithId) = withContext(mainDispatcher) {
        PlayerService.start(context, clickTrack)
    }

    override suspend fun stop(): Unit = withContext(mainDispatcher) {
        PlayerService.stop(context)
    }

    override fun playbackState(): Flow<PlaybackState?> {
        return binderState.flatMapLatest { it?.playbackState ?: flowOf(null) }
    }
}
