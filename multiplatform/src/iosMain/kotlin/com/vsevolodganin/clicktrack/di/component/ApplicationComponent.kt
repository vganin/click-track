package com.vsevolodganin.clicktrack.di.component

import com.vsevolodganin.clicktrack.common.BuildConfig
import com.vsevolodganin.clicktrack.common.BuildConfigImpl
import com.vsevolodganin.clicktrack.utils.log.Logger
import com.vsevolodganin.clicktrack.utils.log.LoggerImpl
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(ApplicationScope::class)
interface ApplicationComponent {

    val mainViewApplicationComponentFactory: MainViewControllerComponent.Factory

    @Binds
    val BuildConfigImpl.binding: BuildConfig

    @Binds
    val LoggerImpl.binding: Logger
}
