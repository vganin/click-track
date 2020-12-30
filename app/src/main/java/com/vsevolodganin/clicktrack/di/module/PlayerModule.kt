package com.vsevolodganin.clicktrack.di.module

import com.vsevolodganin.clicktrack.player.Player
import com.vsevolodganin.clicktrack.player.PlayerImpl
import com.vsevolodganin.clicktrack.player.PlayerServiceAccess
import dagger.Binds
import dagger.Module

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
