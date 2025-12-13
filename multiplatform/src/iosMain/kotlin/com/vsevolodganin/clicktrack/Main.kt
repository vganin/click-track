package com.vsevolodganin.clicktrack

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.vsevolodganin.clicktrack.di.component.ApplicationComponent
import com.vsevolodganin.clicktrack.ui.RootView
import dev.zacsweers.metro.createGraph

// TODO: 🚧 Under heavy construction 🚧
fun mainViewController() = ComposeUIViewController {
    val rootViewModel = remember {
        createGraph<ApplicationComponent>()
            .mainViewApplicationComponentFactory
            .create(DefaultComponentContext(LifecycleRegistry()))
            .also {
                it.migrationManager.tryMigrate()
            }
            .rootViewModel
    }

    RootView(rootViewModel)
}
