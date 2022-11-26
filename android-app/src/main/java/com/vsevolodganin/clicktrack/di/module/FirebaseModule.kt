package com.vsevolodganin.clicktrack.di.module

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebase(application: Application): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(application)
    }
}
