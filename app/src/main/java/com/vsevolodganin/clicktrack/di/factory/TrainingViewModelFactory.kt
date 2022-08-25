package com.vsevolodganin.clicktrack.di.factory

import com.arkivanov.decompose.ComponentContext
import com.vsevolodganin.clicktrack.training.TrainingViewModelImpl
import dagger.assisted.AssistedFactory

@AssistedFactory
interface TrainingViewModelFactory {
    fun create(componentContext: ComponentContext): TrainingViewModelImpl
}
