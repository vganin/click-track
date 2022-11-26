package com.vsevolodganin.clicktrack.di.factory

import com.arkivanov.decompose.ComponentContext
import com.vsevolodganin.clicktrack.drawer.DrawerViewModelImpl
import dagger.assisted.AssistedFactory

@AssistedFactory
interface DrawerViewModelFactory {
    fun create(componentContext: ComponentContext): DrawerViewModelImpl
}
