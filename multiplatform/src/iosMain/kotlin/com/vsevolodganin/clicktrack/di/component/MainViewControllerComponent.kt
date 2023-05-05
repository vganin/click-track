package com.vsevolodganin.clicktrack.di.component

import com.arkivanov.decompose.ComponentContext
import com.vsevolodganin.clicktrack.RootViewModel
import com.vsevolodganin.clicktrack.ScreenViewModelFactory
import com.vsevolodganin.clicktrack.ScreenViewModelFactoryImpl
import com.vsevolodganin.clicktrack.di.module.ViewModelModule
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@MainControllerScope
@Component
abstract class MainViewControllerComponent(
    @get:Provides protected val componentContext: ComponentContext
) : ViewModelModule {
    abstract val rootViewModel: RootViewModel

    @get:Provides
    protected val ScreenViewModelFactoryImpl.screenViewModelFactory: ScreenViewModelFactory get() = this
}
