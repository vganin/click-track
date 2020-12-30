package com.vsevolodganin.clicktrack.player

import android.os.Binder
import com.vsevolodganin.clicktrack.state.PlaybackState
import kotlinx.coroutines.flow.Flow

class PlayerServiceBinder(
    val playbackState: Flow<PlaybackState?>
) : Binder()
