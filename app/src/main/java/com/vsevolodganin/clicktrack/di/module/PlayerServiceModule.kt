package com.vsevolodganin.clicktrack.di.module

import android.os.Process
import com.vsevolodganin.clicktrack.di.component.PlayerServiceScope
import com.vsevolodganin.clicktrack.utils.coroutine.createSingleThreadCoroutineDispatcher
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

@Qualifier
annotation class PlayerDispatcher

@Module
object PlayerServiceModule {

    @Provides
    @PlayerServiceScope
    fun providePlayerServiceCoroutineScope(): CoroutineScope {
        return CoroutineScope(Dispatchers.Main.immediate)
    }

    @Provides
    @PlayerServiceScope
    @PlayerDispatcher
    fun providePlayerCoroutineDispatcher(): CoroutineDispatcher {
        return createSingleThreadCoroutineDispatcher("ClickTrackPlayer", Process.THREAD_PRIORITY_URGENT_AUDIO)
    }
}
