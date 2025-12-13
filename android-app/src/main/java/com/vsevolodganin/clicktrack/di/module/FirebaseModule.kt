package com.vsevolodganin.clicktrack.di.module

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(ApplicationScope::class)
@BindingContainer
object FirebaseModule {

    @Provides
    @SingleIn(ApplicationScope::class)
    fun provideCrashlytics(): FirebaseCrashlytics {
        return FirebaseCrashlytics.getInstance()
    }
}
