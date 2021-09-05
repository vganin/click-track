package com.vsevolodganin.clicktrack.state.redux.epic

import android.app.Activity
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewManager
import com.vsevolodganin.clicktrack.di.component.ActivityScoped
import com.vsevolodganin.clicktrack.state.redux.action.InAppReviewAction
import com.vsevolodganin.clicktrack.state.redux.core.Action
import com.vsevolodganin.clicktrack.state.redux.core.Epic
import com.vsevolodganin.clicktrack.utils.flow.consumeEach
import javax.inject.Inject
import javax.inject.Provider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import timber.log.Timber

@ActivityScoped
class InAppReviewEpic @Inject constructor(
    private val reviewManagerProvider: Provider<ReviewManager>,
    private val activity: Activity,
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return actions.filterIsInstance<InAppReviewAction.RequestReview>()
            .consumeEach {
                try {
                    val reviewManager = reviewManagerProvider.get()
                    val reviewInfo = reviewManager.requestReview()
                    reviewManager.launchReview(activity, reviewInfo)
                } catch (t: Throwable) {
                    Timber.e(t, "Failed to request review")
                }
            }
    }
}
