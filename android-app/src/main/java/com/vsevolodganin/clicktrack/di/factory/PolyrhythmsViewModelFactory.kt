package com.vsevolodganin.clicktrack.di.factory

import com.arkivanov.decompose.ComponentContext
import com.vsevolodganin.clicktrack.polyrhythm.PolyrhythmsViewModelImpl
import dagger.assisted.AssistedFactory

@AssistedFactory
interface PolyrhythmsViewModelFactory {
    fun create(componentContext: ComponentContext): PolyrhythmsViewModelImpl
}
