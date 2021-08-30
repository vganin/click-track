package com.vsevolodganin.clicktrack.di.module

import android.os.Process
import com.vsevolodganin.clicktrack.di.component.ApplicationScoped
import com.vsevolodganin.clicktrack.di.component.ViewModelScoped
import dagger.Module
import dagger.Provides
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Qualifier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SerialBackgroundDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
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
        return Executors.newSingleThreadExecutor { runnable -> Thread(runnable, "ClickTrackSerialBackground") }
            .also { it.setThreadPriority(Process.THREAD_PRIORITY_DEFAULT) }
            .asCoroutineDispatcher()
    }

    @Provides
    @ApplicationScoped
    @PlayerDispatcher
    fun providePlayerDispatcher(): CoroutineDispatcher {
        return Executors.newSingleThreadExecutor { runnable -> Thread(runnable, "ClickTrackPlayer") }
            .also { it.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO) }
            .asCoroutineDispatcher()
    }

    private fun ExecutorService.setThreadPriority(threadPriority: Int) {
        execute {
            Process.setThreadPriority(threadPriority)
        }
    }
}

@Module
class ViewModelScopedCoroutineModule {

    @Provides
    @ViewModelScoped
    fun provideCoroutineScope(@MainDispatcher dispatcher: CoroutineDispatcher) = CoroutineScope(dispatcher)
}
