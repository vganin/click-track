package com.vsevolodganin.clicktrack.di.module

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import me.tatarka.inject.annotations.Provides

interface FirebaseModule {

    @Provides
    @ApplicationScope
    fun provideCrashlytics(): FirebaseCrashlytics {
        return FirebaseCrashlytics.getInstance()
    }
}
