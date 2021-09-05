package com.vsevolodganin.clicktrack.state.redux.core

import com.vsevolodganin.clicktrack.analytics.AnalyticsLogger

class AnalyticsMiddleware<T>(
    private val analyticsLogger: AnalyticsLogger,
) : Middleware<T> {

    override fun interfere(store: Store<T>, dispatch: SuspendDispatch): SuspendDispatch {
        return SuspendDispatch { action ->
            logAction(action)
            dispatch(action)
        }
    }

    private fun logAction(action: Action) {
        analyticsLogger.logEvent(
            "redux_action",
            "class" to action.javaClass.analyticsName()
        )
    }

    private fun Class<*>.analyticsName() = name.substring(name.lastIndexOf('.').coerceAtLeast(0))
}
