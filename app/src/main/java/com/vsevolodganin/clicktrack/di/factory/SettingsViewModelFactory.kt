package com.vsevolodganin.clicktrack.di.factory

import com.arkivanov.decompose.ComponentContext
import com.vsevolodganin.clicktrack.settings.SettingsViewModelImpl
import dagger.assisted.AssistedFactory

@AssistedFactory
interface SettingsViewModelFactory {
    fun create(componentContext: ComponentContext): SettingsViewModelImpl
}
