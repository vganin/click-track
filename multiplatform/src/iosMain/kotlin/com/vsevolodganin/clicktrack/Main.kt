package com.vsevolodganin.clicktrack

import com.arkivanov.decompose.ComponentContext
import com.vsevolodganin.clicktrack.di.component.ApplicationComponent
import com.vsevolodganin.clicktrack.player.AudioSessionNotification
import dev.zacsweers.metro.createGraph
import platform.UIKit.UIViewController

fun createApplicationComponent(): ApplicationComponent = createGraph()

fun createMainViewController(
    applicationComponent: ApplicationComponent,
    componentContext: ComponentContext,
    audioSessionNotification: AudioSessionNotification,
): UIViewController {
    return applicationComponent
        .mainViewApplicationComponentFactory
        .create(componentContext, audioSessionNotification)
        .mainViewController
}
