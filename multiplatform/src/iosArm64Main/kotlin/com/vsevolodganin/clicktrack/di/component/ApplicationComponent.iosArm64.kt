package com.vsevolodganin.clicktrack.di.component

import kotlin.reflect.KClass

actual fun KClass<ApplicationComponent>.createKmp(): ApplicationComponent = ApplicationComponent::class.create()
