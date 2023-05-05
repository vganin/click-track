package com.vsevolodganin.clicktrack.di.component

import com.arkivanov.decompose.ComponentContext
import com.vsevolodganin.clicktrack.RootViewModel
import com.vsevolodganin.clicktrack.RootViewModelImpl
import com.vsevolodganin.clicktrack.ScreenStackNavigation
import com.vsevolodganin.clicktrack.ScreenStackState
import com.vsevolodganin.clicktrack.drawer.DrawerViewModel
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@MainControllerScope
@Component
abstract class MainViewControllerComponent(
    @get:Provides protected val componentContext: ComponentContext,
    @get:Provides val drawerViewModel: DrawerViewModel,
    @get:Provides protected val screenStackNavigation: ScreenStackNavigation,
    @get:Provides protected val screenStackState: ScreenStackState,
) {
    abstract val rootViewModel: RootViewModel

    @get:MainControllerScope
    @get:Provides
    val RootViewModelImpl.rootViewModel: RootViewModel get() = this
}
