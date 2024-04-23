package com.vsevolodganin.clicktrack.common

import android.app.Activity
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewManager
import com.vsevolodganin.clicktrack.analytics.AnalyticsLogger
import com.vsevolodganin.clicktrack.di.component.MainControllerScope
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.utils.log.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds

@MainControllerScope
@Inject
class InAppReview(
    private val reviewManagerProvider: () -> ReviewManager,
    private val activity: Activity,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val analyticsLogger: AnalyticsLogger,
    private val logger: Logger,
) {
    fun tryLaunchRequestReview() {
        GlobalScope.launch(Dispatchers.Main) {
            tryRequestReview()
        }
    }

    private suspend fun tryRequestReview() {
        try {
            if (mayRequestReview()) {
                val reviewManager = reviewManagerProvider.invoke()
                val reviewInfo = reviewManager.requestReview()
                reviewManager.launchReview(activity, reviewInfo)
                userPreferencesRepository.reviewRequestTimestamp.value = nowMilliseconds()
                analyticsLogger.logEvent("review_requested")
            }
        } catch (t: Throwable) {
            logger.logError(TAG, "Failed to request review", t)
        }
    }

    private fun mayRequestReview(): Boolean {
        val now = nowMilliseconds()
        val reviewRequestTimestamp = userPreferencesRepository.reviewRequestTimestamp.value

        if (reviewRequestTimestamp == UserPreferencesRepository.Const.REVIEW_NOT_REQUESTED) {
            userPreferencesRepository.reviewRequestTimestamp.value = now
            return false
        }

        return (now - reviewRequestTimestamp).milliseconds >= REVIEW_REQUEST_PERIOD
    }

    private fun nowMilliseconds(): Long = System.currentTimeMillis()

    private companion object Const {
        const val TAG = "InAppReview"
        val REVIEW_REQUEST_PERIOD = 30.days
    }
}
