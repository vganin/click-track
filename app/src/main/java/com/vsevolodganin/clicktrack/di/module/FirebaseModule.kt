package com.vsevolodganin.clicktrack.di.module

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebase(context: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }
}
