package com.vsevolodganin.clicktrack.di.module

import android.os.Process
import com.vsevolodganin.clicktrack.di.component.ApplicationScoped
import com.vsevolodganin.clicktrack.di.component.ViewModelScoped
import com.vsevolodganin.clicktrack.utils.coroutine.createSingleThreadCoroutineDispatcher
import dagger.Module
import dagger.Provides
import javax.inject.Qualifier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@Qualifier
annotation class SerialBackgroundDispatcher

@Qualifier
annotation class MainDispatcher

@Qualifier
annotation class PlayerDispatcher

@Module
class ApplicationScopedCoroutineModule {

    @Provides
    @ApplicationScoped
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @ApplicationScoped
    @SerialBackgroundDispatcher
    fun provideSerialBackgroundDispatcher(): CoroutineDispatcher {
        return createSingleThreadCoroutineDispatcher("ClickTrackSerialBackground", Process.THREAD_PRIORITY_DEFAULT)
    }

    @Provides
    @ApplicationScoped
    @PlayerDispatcher
    fun providePlayerDispatcher(): CoroutineDispatcher {
        return createSingleThreadCoroutineDispatcher("ClickTrackPlayer", Process.THREAD_PRIORITY_URGENT_AUDIO)
    }
}

@Module
class ViewModelScopedCoroutineModule {

    @Provides
    @ViewModelScoped
    fun provideCoroutineScope(@MainDispatcher dispatcher: CoroutineDispatcher) = CoroutineScope(dispatcher)
}
