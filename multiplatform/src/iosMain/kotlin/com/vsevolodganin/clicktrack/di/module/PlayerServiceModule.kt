package com.vsevolodganin.clicktrack.di.module

import com.vsevolodganin.clicktrack.di.component.PlayerServiceScope
import com.vsevolodganin.clicktrack.player.PlayerDispatcher
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext

@ContributesTo(PlayerServiceScope::class)
@BindingContainer
object PlayerServiceModule {

    @Provides
    @SingleIn(PlayerServiceScope::class)
    fun providePlayerServiceCoroutineScope(): CoroutineScope {
        return CoroutineScope(Dispatchers.Main.immediate)
    }

    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    @Provides
    @SingleIn(PlayerServiceScope::class)
    fun providePlayerCoroutineDispatcher(): PlayerDispatcher {
        return newSingleThreadContext("ClickTrackPlayer") // TODO: Make it highest priority like on Android?
    }
}
