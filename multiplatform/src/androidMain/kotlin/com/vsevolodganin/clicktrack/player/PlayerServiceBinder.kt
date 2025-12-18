package com.vsevolodganin.clicktrack.player

import android.os.Binder
import kotlinx.coroutines.flow.Flow

class PlayerServiceBinder(
    val playbackState: Flow<PlaybackState?>,
) : Binder()
