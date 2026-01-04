package com.vsevolodganin.clicktrack.di.module

import cocoapods.FirebaseCrashlytics.FIRCrashlytics
import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.cinterop.ExperimentalForeignApi

@ContributesTo(ApplicationScope::class)
@BindingContainer
object FirebaseModule {

    @OptIn(ExperimentalForeignApi::class)
    @Provides
    @SingleIn(ApplicationScope::class)
    fun provideCrashlytics(): FIRCrashlytics {
        return FIRCrashlytics.crashlytics()
    }
}
