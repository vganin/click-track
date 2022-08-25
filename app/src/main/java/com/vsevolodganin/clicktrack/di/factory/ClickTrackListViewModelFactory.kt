package com.vsevolodganin.clicktrack.di.factory

import com.arkivanov.decompose.ComponentContext
import com.vsevolodganin.clicktrack.list.ClickTrackListViewModelImpl
import dagger.assisted.AssistedFactory

@AssistedFactory
interface ClickTrackListViewModelFactory {
    fun create(componentContext: ComponentContext): ClickTrackListViewModelImpl
}
