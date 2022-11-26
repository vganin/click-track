package com.vsevolodganin.clicktrack.di.factory

import com.arkivanov.decompose.ComponentContext
import com.vsevolodganin.clicktrack.soundlibrary.SoundLibraryViewModelImpl
import dagger.assisted.AssistedFactory

@AssistedFactory
interface SoundLibraryViewModelFactory {
    fun create(componentContext: ComponentContext): SoundLibraryViewModelImpl
}
