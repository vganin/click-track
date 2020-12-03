package net.ganin.vsevolod.clicktrack.di.module

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import net.ganin.vsevolod.clicktrack.di.component.ApplicationScoped
import net.ganin.vsevolod.clicktrack.di.component.PlayerServiceScoped
import net.ganin.vsevolod.clicktrack.di.component.ViewModelScoped
import java.util.concurrent.Executors
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ComputationDispatcher

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
    @ComputationDispatcher
    fun provideComputationDispatcher(): CoroutineDispatcher = Dispatchers.Default
}

@Module
class ViewModelScopedCoroutineModule {

    @Provides
    @ViewModelScoped
    fun provideCoroutineScope(@MainDispatcher dispatcher: CoroutineDispatcher) = CoroutineScope(dispatcher)
}

@Module
class PlayerServiceScopedCoroutineModule {

    @Provides
    @PlayerServiceScoped
    @PlayerDispatcher
    fun providePlayerDispatcher(): CoroutineDispatcher {
        return Executors.newSingleThreadExecutor { runnable ->
            Thread(runnable, "ClickTrackPlayer").apply {
                priority = Thread.MAX_PRIORITY
            }
        }.asCoroutineDispatcher()
    }
}
