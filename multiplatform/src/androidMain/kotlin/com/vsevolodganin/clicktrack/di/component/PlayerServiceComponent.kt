package com.vsevolodganin.clicktrack.di.component

import com.vsevolodganin.clicktrack.player.PlayerService
import dev.zacsweers.metro.GraphExtension

@GraphExtension(PlayerServiceScope::class)
interface PlayerServiceComponent {

    fun inject(playerService: PlayerService)

    @GraphExtension.Factory
    fun interface Factory {
        fun create(): PlayerServiceComponent
    }
}
