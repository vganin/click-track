package com.vsevolodganin.clicktrack.di.factory

import com.arkivanov.decompose.ComponentContext
import com.vsevolodganin.clicktrack.ScreenConfiguration
import com.vsevolodganin.clicktrack.play.PlayClickTrackViewModelImpl
import dagger.assisted.AssistedFactory

@AssistedFactory
interface PlayClickTrackViewModelFactory {
    fun create(
        componentContext: ComponentContext,
        config: ScreenConfiguration.PlayClickTrack
    ): PlayClickTrackViewModelImpl
}
