package com.vsevolodganin.clicktrack.di.factory

import com.arkivanov.decompose.ComponentContext
import com.vsevolodganin.clicktrack.about.AboutViewModelImpl
import dagger.assisted.AssistedFactory

@AssistedFactory
interface AboutViewModelFactory {
    fun create(componentContext: ComponentContext): AboutViewModelImpl
}
