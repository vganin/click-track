package com.vsevolodganin.clicktrack.di.component

import dev.zacsweers.metro.createGraphFactory
import kotlin.reflect.KClass

actual fun KClass<ApplicationComponent>.createKmp(): ApplicationComponent = createGraphFactory<ApplicationComponent.Factory>().create()
