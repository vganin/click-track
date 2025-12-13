package com.vsevolodganin.clicktrack.di.module

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.essenty.instancekeeper.InstanceKeeperOwner
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.statekeeper.StateKeeperOwner
import com.vsevolodganin.clicktrack.RootViewModel
import com.vsevolodganin.clicktrack.RootViewModelImpl
import com.vsevolodganin.clicktrack.ScreenConfiguration
import com.vsevolodganin.clicktrack.ScreenStackNavigation
import com.vsevolodganin.clicktrack.ScreenStackState
import com.vsevolodganin.clicktrack.ScreenViewModelFactory
import com.vsevolodganin.clicktrack.di.component.MainControllerScope
import com.vsevolodganin.clicktrack.drawer.DrawerNavigation
import com.vsevolodganin.clicktrack.drawer.DrawerNavigationImpl
import com.vsevolodganin.clicktrack.drawer.DrawerNavigationSource
import com.vsevolodganin.clicktrack.drawer.DrawerViewModel
import com.vsevolodganin.clicktrack.drawer.DrawerViewModelImpl
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.serialization.serializer

@ContributesTo(MainControllerScope::class)
@BindingContainer
interface ViewModelModule {

    @Binds
    fun provideRootViewModel(rootViewModelImpl: RootViewModelImpl): RootViewModel

    @Binds
    fun provideLifecycleOwner(componentContext: ComponentContext): LifecycleOwner

    @Binds
    fun provideStateKeeperOwner(componentContext: ComponentContext): StateKeeperOwner

    @Binds
    fun provideInstanceKeeperOwner(componentContext: ComponentContext): InstanceKeeperOwner

    companion object {
        @Provides
        @SingleIn(MainControllerScope::class)
        fun provideDrawerNavigationImpl(): DrawerNavigationImpl = DrawerNavigationImpl()

        @Provides
        @SingleIn(MainControllerScope::class)
        fun provideScreenStackNavigation(): ScreenStackNavigation = StackNavigation()

        @Provides
        @SingleIn(MainControllerScope::class)
        fun provideDrawerNavigation(drawerNavigationImpl: DrawerNavigationImpl): DrawerNavigation = drawerNavigationImpl

        @Provides
        @SingleIn(MainControllerScope::class)
        fun provideDrawerNavigationSource(drawerNavigationImpl: DrawerNavigationImpl): DrawerNavigationSource = drawerNavigationImpl

        @Provides
        @SingleIn(MainControllerScope::class)
        fun provideScreenStackState(
            componentContext: ComponentContext,
            stackNavigation: ScreenStackNavigation,
            screenViewModelFactory: ScreenViewModelFactory,
        ): ScreenStackState = componentContext.childStack(
            source = stackNavigation,
            initialStack = { listOf(ScreenConfiguration.ClickTrackList) },
            childFactory = screenViewModelFactory::create,
            serializer = serializer(),
        )

        @Provides
        @SingleIn(MainControllerScope::class)
        fun provideDrawerViewModel(
            componentContext: ComponentContext,
            drawerViewModelFactory: DrawerViewModelImpl.Factory,
        ): DrawerViewModel = drawerViewModelFactory.create(componentContext.childContext("Drawer"))
    }
}
