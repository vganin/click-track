package com.vsevolodganin.clicktrack.di.component

import com.arkivanov.decompose.ComponentContext
import com.vsevolodganin.clicktrack.player.AudioSessionNotification
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import platform.UIKit.UIViewController

@GraphExtension(
    scope = MainControllerScope::class,
    additionalScopes = [PlayerServiceScope::class],
)
interface MainViewControllerComponent {

    val mainViewController: UIViewController

    @GraphExtension.Factory
    fun interface Factory {
        fun create(
            @Provides componentContext: ComponentContext,
            @Provides audioSessionNotification: AudioSessionNotification,
        ): MainViewControllerComponent
    }
}
