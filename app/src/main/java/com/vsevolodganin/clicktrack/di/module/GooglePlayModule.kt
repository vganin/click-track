package com.vsevolodganin.clicktrack.di.module

import android.content.Context
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.Module
import dagger.Provides

@Module
object GooglePlayModule {

    @Provides
    fun provideReviewManager(context: Context): ReviewManager {
        return ReviewManagerFactory.create(context)
    }
}
