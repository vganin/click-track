package com.vsevolodganin.clicktrack.utils.log

import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import me.tatarka.inject.annotations.Inject

@ApplicationScope
@Inject
class LoggerImpl : Logger {
    override fun logError(
        tag: String,
        message: String,
    ) = logError(tag, message, null)

    override fun logError(
        tag: String,
        message: String,
        throwable: Throwable?,
    ) {
        println(
            if (throwable == null) {
                "$tag: $message"
            } else {
                "$tag: $message: ${throwable.stackTraceToString()}"
            },
        )
    }
}
