package com.vsevolodganin.clicktrack.di.module

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.essenty.instancekeeper.InstanceKeeperOwner
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.statekeeper.StateKeeperOwner
import com.vsevolodganin.clicktrack.Navigation
import com.vsevolodganin.clicktrack.RootViewModel
import com.vsevolodganin.clicktrack.RootViewModelImpl
import com.vsevolodganin.clicktrack.ScreenConfiguration
import com.vsevolodganin.clicktrack.ScreenStackNavigation
import com.vsevolodganin.clicktrack.ScreenStackState
import com.vsevolodganin.clicktrack.ScreenViewModelFactory
import com.vsevolodganin.clicktrack.di.component.ActivityScope
import com.vsevolodganin.clicktrack.drawer.DrawerNavigation
import com.vsevolodganin.clicktrack.drawer.DrawerViewModel
import com.vsevolodganin.clicktrack.drawer.DrawerViewModelImpl
import me.tatarka.inject.annotations.Provides

interface ViewModelModule {

    @Provides
    @ActivityScope
    fun provideRootViewModel(rootViewModelImpl: RootViewModelImpl): RootViewModel = rootViewModelImpl

    @Provides
    @ActivityScope
    fun provideDrawerNavigation(drawerViewModel: DrawerViewModel): DrawerNavigation = drawerViewModel

    @Provides
    @ActivityScope
    fun provideLifecycleOwner(componentContext: ComponentContext): LifecycleOwner = componentContext

    @Provides
    @ActivityScope
    fun provideStateKeeperOwner(componentContext: ComponentContext): StateKeeperOwner = componentContext

    @Provides
    @ActivityScope
    fun provideInstanceKeeperOwner(componentContext: ComponentContext): InstanceKeeperOwner = componentContext

    @Provides
    @ActivityScope
    fun provideStackNavigation(): ScreenStackNavigation = StackNavigation()

    @Provides
    @ActivityScope
    fun provideScreenStackState(
        componentContext: ComponentContext,
        stackNavigation: ScreenStackNavigation,
        screenViewModelFactory: ScreenViewModelFactory
    ): ScreenStackState = componentContext.childStack(
        source = stackNavigation,
        initialStack = { listOf(ScreenConfiguration.ClickTrackList) },
        childFactory = screenViewModelFactory::create,
    )

    @Provides
    @ActivityScope
    fun provideDrawerViewModel(
        componentContext: ComponentContext,
        drawerViewModelFactory: (ComponentContext) -> DrawerViewModelImpl,
    ): DrawerViewModel = drawerViewModelFactory.invoke(componentContext.childContext("Drawer"))

    @Provides
    @ActivityScope
    fun provideNavigation(
        stackNavigation: ScreenStackNavigation,
        drawerNavigation: DrawerNavigation
    ): Navigation = Navigation(stackNavigation, drawerNavigation)
}
