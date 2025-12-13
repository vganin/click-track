package com.vsevolodganin.clicktrack.di.module

import android.app.Application
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.vsevolodganin.clicktrack.di.component.MainControllerScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(MainControllerScope::class)
@BindingContainer
object GooglePlayModule {

    @Provides
    fun provideReviewManager(application: Application): ReviewManager = ReviewManagerFactory.create(application)
}
