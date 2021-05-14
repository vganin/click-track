package com.vsevolodganin.clicktrack.di.component

import com.vsevolodganin.clicktrack.di.module.PlayerServiceScopedPlayerModule
import com.vsevolodganin.clicktrack.player.PlayerService
import dagger.Subcomponent
import javax.inject.Scope

@Scope
annotation class PlayerServiceScoped

@PlayerServiceScoped
@Subcomponent(
    modules = [
        PlayerServiceScopedPlayerModule::class
    ]
)
interface PlayerServiceComponent {
    fun inject(playerService: PlayerService)

    @Subcomponent.Builder
    interface Builder {
        fun build(): PlayerServiceComponent
    }
}
