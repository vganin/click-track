package com.vsevolodganin.clicktrack.di.module

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.essenty.statekeeper.StateKeeper
import com.vsevolodganin.clicktrack.Navigation
import com.vsevolodganin.clicktrack.RootViewModel
import com.vsevolodganin.clicktrack.RootViewModelImpl
import com.vsevolodganin.clicktrack.ScreenConfiguration
import com.vsevolodganin.clicktrack.ScreenStackNavigation
import com.vsevolodganin.clicktrack.ScreenStackState
import com.vsevolodganin.clicktrack.di.component.ActivityScope
import com.vsevolodganin.clicktrack.di.factory.DrawerViewModelFactory
import com.vsevolodganin.clicktrack.di.factory.ScreenViewModelFactory
import com.vsevolodganin.clicktrack.drawer.DrawerNavigation
import com.vsevolodganin.clicktrack.drawer.DrawerViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface ViewModelModule {

    @Binds
    @ActivityScope
    fun provideRootViewModel(rootViewModelImpl: RootViewModelImpl): RootViewModel

    @Binds
    @ActivityScope
    fun provideDrawerNavigation(drawerViewModel: DrawerViewModel): DrawerNavigation

    companion object {

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
            drawerViewModelFactory: DrawerViewModelFactory,
        ): DrawerViewModel = drawerViewModelFactory.create(componentContext.childContext("Drawer"))

        @Provides
        @ActivityScope
        fun provideNavigation(
            stackNavigation: ScreenStackNavigation,
            drawerNavigation: DrawerNavigation
        ): Navigation = Navigation(stackNavigation, drawerNavigation)

        @Provides
        @ActivityScope
        fun provideStateKeeper(componentContext: ComponentContext): StateKeeper = componentContext.stateKeeper
    }
}
