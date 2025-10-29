package com.vsevolodganin.clicktrack.di.component

import com.arkivanov.decompose.ComponentContext
import kotlin.reflect.KClass

actual fun KClass<MainViewControllerComponent>.createKmp(
    applicationComponent: ApplicationComponent,
    componentContext: ComponentContext,
): MainViewControllerComponent = MainViewControllerComponent::class.create(
    applicationComponent,
    componentContext,
)
