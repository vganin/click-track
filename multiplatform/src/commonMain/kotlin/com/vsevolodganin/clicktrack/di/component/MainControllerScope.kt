package com.vsevolodganin.clicktrack.di.component

import dev.zacsweers.metro.Scope

@Scope
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
annotation class MainControllerScope
