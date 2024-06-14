package com.vsevolodganin.clicktrack.utils.log

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.vsevolodganin.clicktrack.BuildConfig
import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import me.tatarka.inject.annotations.Inject

@ApplicationScope
@Inject
class LoggerImpl(
    private val firebaseCrashlytics: FirebaseCrashlytics,
) : Logger {
    override fun logError(tag: String, message: String) = logError(tag, message, null)

    override fun logError(tag: String, message: String, throwable: Throwable?) {
        firebaseCrashlytics.setCustomKey("Tag", tag)
        firebaseCrashlytics.recordException(NonFatalException(message, throwable))

        if (BuildConfig.DEBUG) {
            Log.e("ClickTrack::$tag", message, throwable)
        }
    }

    private class NonFatalException(message: String, throwable: Throwable?) : Exception(message, throwable)
}
