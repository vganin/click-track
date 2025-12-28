package com.vsevolodganin.clicktrack.di.module

import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.window.ComposeUIViewController
import com.vsevolodganin.clicktrack.RootViewModel
import com.vsevolodganin.clicktrack.di.component.MainControllerScope
import com.vsevolodganin.clicktrack.migration.MigrationManager
import com.vsevolodganin.clicktrack.ui.RootView
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import platform.UIKit.UIViewController

@ContributesTo(MainControllerScope::class)
@BindingContainer
class MainViewControllerModule {

    @SingleIn(MainControllerScope::class)
    @Provides
    fun provideMainViewController(
        rootViewModel: RootViewModel,
        migrationManager: MigrationManager,
    ): UIViewController {
        return ComposeUIViewController {
            DisposableEffect(Unit) {
                migrationManager.tryMigrate()
                onDispose {}
            }

            RootView(rootViewModel)
        }
    }
}
