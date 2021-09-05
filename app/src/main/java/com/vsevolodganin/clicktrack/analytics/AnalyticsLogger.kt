package com.vsevolodganin.clicktrack.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsLogger @Inject constructor(private val firebaseAnalytics: FirebaseAnalytics) {

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
