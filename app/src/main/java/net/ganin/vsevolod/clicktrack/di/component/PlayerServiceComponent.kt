package net.ganin.vsevolod.clicktrack.di.component

import dagger.Subcomponent
import net.ganin.vsevolod.clicktrack.di.module.PlayerServiceScopedCoroutineModule
import net.ganin.vsevolod.clicktrack.di.module.PlayerServiceScopedPlayerModule
import net.ganin.vsevolod.clicktrack.player.PlayerService
import javax.inject.Scope

@Scope
annotation class PlayerServiceScoped

@PlayerServiceScoped
@Subcomponent(
    modules = [
        PlayerServiceScopedCoroutineModule::class,
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
