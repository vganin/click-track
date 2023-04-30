package com.vsevolodganin.clicktrack.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import me.tatarka.inject.annotations.Inject

@ApplicationScope
@Inject
class AnalyticsLogger(private val firebaseAnalytics: FirebaseAnalytics) {

    fun logEvent(name: String, vararg parameters: Pair<String, String>) {
        firebaseAnalytics.logEvent(
            name,
            Bundle().apply {
                parameters.forEach { (name, value) ->
                    putString(name, value)
                }
            }
        )
    }
}
