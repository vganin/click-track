package com.vsevolodganin.clicktrack.di.module

import android.os.Process
import com.vsevolodganin.clicktrack.di.component.PlayerServiceScope
import com.vsevolodganin.clicktrack.utils.coroutine.createSingleThreadCoroutineDispatcher
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

typealias PlayerDispatcher = CoroutineDispatcher

@ContributesTo(PlayerServiceScope::class)
@BindingContainer
object PlayerServiceModule {

    @Provides
    @SingleIn(PlayerServiceScope::class)
    fun providePlayerServiceCoroutineScope(): CoroutineScope {
        return CoroutineScope(Dispatchers.Main.immediate)
    }

    @Provides
    @SingleIn(PlayerServiceScope::class)
    fun providePlayerCoroutineDispatcher(): PlayerDispatcher {
        return createSingleThreadCoroutineDispatcher("ClickTrackPlayer", Process.THREAD_PRIORITY_URGENT_AUDIO)
    }
}
