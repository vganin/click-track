package com.vsevolodganin.clicktrack.di.component

import com.arkivanov.decompose.ComponentContext
import dev.zacsweers.metro.createGraphFactory
import kotlin.reflect.KClass

actual fun KClass<MainViewControllerComponent>.createKmp(
    applicationComponent: ApplicationComponent,
    componentContext: ComponentContext,
): MainViewControllerComponent = createGraphFactory<MainViewControllerComponent.Factory>().create(
    applicationComponent,
    componentContext,
)
