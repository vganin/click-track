package com.vsevolodganin.clicktrack.di.module

import android.os.Process
import com.vsevolodganin.clicktrack.di.component.PlayerServiceScope
import com.vsevolodganin.clicktrack.utils.coroutine.createSingleThreadCoroutineDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import me.tatarka.inject.annotations.Provides

typealias PlayerDispatcher = CoroutineDispatcher

interface PlayerServiceModule {

    @Provides
    @PlayerServiceScope
    fun providePlayerServiceCoroutineScope(): CoroutineScope {
        return CoroutineScope(Dispatchers.Main.immediate)
    }

    @Provides
    @PlayerServiceScope
    fun providePlayerCoroutineDispatcher(): PlayerDispatcher {
        return createSingleThreadCoroutineDispatcher("ClickTrackPlayer", Process.THREAD_PRIORITY_URGENT_AUDIO)
    }
}
