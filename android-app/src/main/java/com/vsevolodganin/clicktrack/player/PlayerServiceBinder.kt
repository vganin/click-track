package com.vsevolodganin.clicktrack.player

import android.os.Binder

class PlayerServiceBinder(
    val player: Player
) : Binder()
