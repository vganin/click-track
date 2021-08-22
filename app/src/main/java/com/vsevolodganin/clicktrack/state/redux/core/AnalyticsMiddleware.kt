package com.vsevolodganin.clicktrack.state.redux.core

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class AnalyticsMiddleware<T>(
    private val firebaseAnalytics: FirebaseAnalytics,
) : Middleware<T> {

    override fun interfere(store: Store<T>, dispatch: SuspendDispatch): SuspendDispatch {
        return SuspendDispatch { action ->
            logAction(action)
            dispatch(action)
        }
    }

    private fun logAction(action: Action) {
        firebaseAnalytics.logEvent(
            "redux_action",
            Bundle().apply {
                putString("class", action.javaClass.simpleName)
            }
        )
    }
}
