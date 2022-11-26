package com.vsevolodganin.clicktrack.di.component

import com.vsevolodganin.clicktrack.di.module.PlayerServiceModule
import com.vsevolodganin.clicktrack.player.PlayerService
import dagger.Subcomponent
import javax.inject.Scope

@Scope
annotation class PlayerServiceScope

@PlayerServiceScope
@Subcomponent(
    modules = [PlayerServiceModule::class]
)
interface PlayerServiceComponent {
    fun inject(playerService: PlayerService)

    @Subcomponent.Builder
    interface Builder {
        fun build(): PlayerServiceComponent
    }
}
