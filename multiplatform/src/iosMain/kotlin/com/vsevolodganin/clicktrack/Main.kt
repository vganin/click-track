package com.vsevolodganin.clicktrack

import com.arkivanov.decompose.ComponentContext
import com.vsevolodganin.clicktrack.di.component.ApplicationComponent
import dev.zacsweers.metro.createGraph
import platform.UIKit.UIViewController

fun createApplicationComponent(): ApplicationComponent = createGraph()

fun createMainViewController(
    applicationComponent: ApplicationComponent,
    componentContext: ComponentContext,
): UIViewController {
    return applicationComponent
        .mainViewApplicationComponentFactory
        .create(componentContext)
        .mainViewController
}
