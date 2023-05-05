package com.vsevolodganin.clicktrack

import com.arkivanov.decompose.ComponentContext

interface ScreenViewModelFactory {
    fun create(screenConfiguration: ScreenConfiguration, componentContext: ComponentContext): ScreenViewModel
}
