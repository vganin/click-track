package com.vsevolodganin.clicktrack.state.redux.epic

import android.app.Activity
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewManager
import com.vsevolodganin.clicktrack.analytics.AnalyticsLogger
import com.vsevolodganin.clicktrack.di.component.ActivityScoped
import com.vsevolodganin.clicktrack.state.redux.action.InAppReviewAction
import com.vsevolodganin.clicktrack.state.redux.core.Action
import com.vsevolodganin.clicktrack.state.redux.core.Epic
import com.vsevolodganin.clicktrack.state.redux.epic.InAppReviewEpic.Const.REVIEW_REQUEST_PERIOD
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.utils.flow.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds

@ActivityScoped
class InAppReviewEpic @Inject constructor(
    private val reviewManagerProvider: Provider<ReviewManager>,
    private val activity: Activity,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val analyticsLogger: AnalyticsLogger,
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return actions.filterIsInstance<InAppReviewAction.RequestReview>()
            .consumeEach {
                requestReview()
            }
    }

    private suspend fun requestReview() {
        try {
            if (mayRequestReview()) {
                val reviewManager = reviewManagerProvider.get()
                val reviewInfo = reviewManager.requestReview()
                reviewManager.launchReview(activity, reviewInfo)
                userPreferencesRepository.reviewRequestTimestamp.edit { nowMilliseconds() }
                analyticsLogger.logEvent("review_requested")
            }
        } catch (t: Throwable) {
            Timber.e(t, "Failed to request review")
        }
    }

    private suspend fun mayRequestReview(): Boolean {
        val now = nowMilliseconds()
        val reviewRequestTimestamp = userPreferencesRepository.reviewRequestTimestamp.flow.first()

        if (reviewRequestTimestamp == UserPreferencesRepository.Const.REVIEW_NOT_REQUESTED) {
            userPreferencesRepository.reviewRequestTimestamp.edit { now }
            return false
        }

        return (now - reviewRequestTimestamp).milliseconds >= REVIEW_REQUEST_PERIOD
    }

    private fun nowMilliseconds(): Long = System.currentTimeMillis()

    private object Const {
        val REVIEW_REQUEST_PERIOD = 7.days
    }
}
