package com.vsevolodganin.clicktrack.di.module

import android.app.Application
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.Module
import dagger.Provides

@Module
object GooglePlayModule {

    @Provides
    fun provideReviewManager(application: Application): ReviewManager {
        return ReviewManagerFactory.create(application)
    }
}
