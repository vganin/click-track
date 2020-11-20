package net.ganin.vsevolod.clicktrack.player

import android.os.Binder
import kotlinx.coroutines.flow.Flow
import net.ganin.vsevolod.clicktrack.state.PlaybackState

class PlayerServiceBinder(
    val playbackState: Flow<PlaybackState?>
) : Binder()
