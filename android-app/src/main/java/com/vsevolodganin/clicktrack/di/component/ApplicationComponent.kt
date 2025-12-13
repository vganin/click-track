package com.vsevolodganin.clicktrack.di.component

import android.app.Application
import com.vsevolodganin.clicktrack.MainApplication
import com.vsevolodganin.clicktrack.common.BuildConfig
import com.vsevolodganin.clicktrack.common.BuildConfigImpl
import com.vsevolodganin.clicktrack.utils.log.Logger
import com.vsevolodganin.clicktrack.utils.log.LoggerImpl
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph(ApplicationScope::class)
interface ApplicationComponent {

    fun inject(mainApplication: MainApplication)

    val mainActivityComponentFactory: MainActivityComponent.Factory
    val exportWorkerComponentFactory: ExportWorkerComponent.Factory
    val playerServiceComponentFactory: PlayerServiceComponent.Factory

    @Binds
    val BuildConfigImpl.binding: BuildConfig

    @Binds
    val LoggerImpl.binding: Logger

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides application: Application): ApplicationComponent
    }
}
