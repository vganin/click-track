package com.vsevolodganin.clicktrack.di.module

import android.app.Application
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import me.tatarka.inject.annotations.Provides

interface GooglePlayModule {

    @Provides
    fun provideReviewManager(application: Application): ReviewManager {
        return ReviewManagerFactory.create(application)
    }
}
