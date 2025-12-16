package com.vsevolodganin.clicktrack.di.component

import android.app.Application
import com.vsevolodganin.clicktrack.MainApplication
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph(ApplicationScope::class)
interface ApplicationComponent {

    fun inject(mainApplication: MainApplication)

    val mainActivityComponentFactory: MainActivityComponent.Factory
    val exportWorkerComponentFactory: ExportWorkerComponent.Factory
    val playerServiceComponentFactory: PlayerServiceComponent.Factory

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides application: Application): ApplicationComponent
    }
}
