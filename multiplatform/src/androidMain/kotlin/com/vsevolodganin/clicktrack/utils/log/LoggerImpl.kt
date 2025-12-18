package com.vsevolodganin.clicktrack.utils.log

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.vsevolodganin.clicktrack.common.ApplicationBuildConfig
import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(ApplicationScope::class)
@ContributesBinding(ApplicationScope::class)
@Inject
class LoggerImpl(
    private val firebaseCrashlytics: FirebaseCrashlytics,
    private val applicationBuildConfig: ApplicationBuildConfig,
) : Logger {
    override fun logError(tag: String, message: String) = logError(tag, message, null)

    override fun logError(tag: String, message: String, throwable: Throwable?) {
        firebaseCrashlytics.setCustomKey("Tag", tag)
        firebaseCrashlytics.recordException(NonFatalException(message, throwable))

        if (applicationBuildConfig.isDebug) {
            Log.e("ClickTrack::$tag", message, throwable)
        }
    }

    private class NonFatalException(message: String, throwable: Throwable?) : Exception(message, throwable)
}
