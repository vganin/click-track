package com.vsevolodganin.clicktrack.di.factory

import com.arkivanov.decompose.ComponentContext
import com.vsevolodganin.clicktrack.metronome.MetronomeViewModelImpl
import dagger.assisted.AssistedFactory

@AssistedFactory
interface MetronomeViewModelFactory {
    fun create(componentContext: ComponentContext): MetronomeViewModelImpl
}
