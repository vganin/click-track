package com.vsevolodganin.clicktrack.state.redux.action

import com.vsevolodganin.clicktrack.state.redux.core.Action

sealed interface InAppReviewAction : Action {

    object RequestReview : InAppReviewAction
}
