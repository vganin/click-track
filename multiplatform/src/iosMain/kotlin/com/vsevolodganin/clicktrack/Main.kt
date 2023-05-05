package com.vsevolodganin.clicktrack

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.vsevolodganin.clicktrack.di.component.MainViewControllerComponent
import com.vsevolodganin.clicktrack.di.component.create
import com.vsevolodganin.clicktrack.ui.RootView

// TODO: 🚧 Under heavy construction 🚧
fun MainViewController() = ComposeUIViewController {
    val component = remember {
        MainViewControllerComponent::class.create(DefaultComponentContext(LifecycleRegistry()))
    }

    RootView(component.rootViewModel)
}
