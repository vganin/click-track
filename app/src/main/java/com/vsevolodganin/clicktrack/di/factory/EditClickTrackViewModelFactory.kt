package com.vsevolodganin.clicktrack.di.factory

import com.arkivanov.decompose.ComponentContext
import com.vsevolodganin.clicktrack.ScreenConfiguration
import com.vsevolodganin.clicktrack.edit.EditClickTrackViewModelImpl
import dagger.assisted.AssistedFactory

@AssistedFactory
interface EditClickTrackViewModelFactory {
    fun create(
        componentContext: ComponentContext,
        config: ScreenConfiguration.EditClickTrack,
    ): EditClickTrackViewModelImpl
}
