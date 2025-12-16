package com.vsevolodganin.clicktrack.di.component

import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(ApplicationScope::class)
interface ApplicationComponent {

    val mainViewApplicationComponentFactory: MainViewControllerComponent.Factory
}
