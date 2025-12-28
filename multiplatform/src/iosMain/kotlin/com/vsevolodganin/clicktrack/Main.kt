package com.vsevolodganin.clicktrack

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.vsevolodganin.clicktrack.di.component.ApplicationComponent
import dev.zacsweers.metro.createGraph
import platform.UIKit.UIViewController

fun createApplicationComponent(): ApplicationComponent = createGraph()

fun createMainViewController(applicationComponent: ApplicationComponent): UIViewController {
    return applicationComponent
        .mainViewApplicationComponentFactory
        .create(DefaultComponentContext(LifecycleRegistry()))
        .mainViewController
}
