package com.vsevolodganin.clicktrack.redux.action

import com.vsevolodganin.clicktrack.redux.core.Action

sealed interface InAppReviewAction : Action {

    object RequestReview : InAppReviewAction
}
