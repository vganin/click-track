package com.vsevolodganin.clicktrack.di.module

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import me.tatarka.inject.annotations.Provides

interface FirebaseModule {

    @Provides
    @ApplicationScope
    fun provideFirebase(application: Application): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(application)
    }
}
