package net.ganin.vsevolod.clicktrack.di.module

import dagger.Binds
import dagger.Module
import net.ganin.vsevolod.clicktrack.player.Player
import net.ganin.vsevolod.clicktrack.player.PlayerImpl
import net.ganin.vsevolod.clicktrack.player.PlayerServiceAccess

@Module
abstract class PlayerServiceScopedPlayerModule {

    @Binds
    abstract fun providePlayer(playerImpl: PlayerImpl): Player
}

@Module
abstract class ViewModelScopedPlayerModule {

    @Binds
    abstract fun providePlayer(playerServiceAccess: PlayerServiceAccess): Player
}
