package com.vsevolodganin.clicktrack.utils.log

import cocoapods.FirebaseCrashlytics.FIRCrashlytics
import cocoapods.FirebaseCrashlytics.FIRExceptionModel
import cocoapods.FirebaseCrashlytics.FIRStackFrame
import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.cinterop.ExperimentalForeignApi
import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalForeignApi::class)
@SingleIn(ApplicationScope::class)
@ContributesBinding(ApplicationScope::class)
@Inject
class LoggerImpl(
    private val crashlytics: FIRCrashlytics,
) : Logger {

    override fun logError(tag: String, message: String) = logError(tag, message, null)

    override fun logError(tag: String, message: String, throwable: Throwable?) {
        crashlytics.setCustomValue("Tag", tag)
        crashlytics.recordExceptionModel(makeExceptionModel(message, throwable))

        println(
            if (throwable == null) {
                "$tag: $message"
            } else {
                "$tag: $message: ${throwable.stackTraceToString()}"
            },
        )
    }

    @OptIn(ExperimentalNativeApi::class)
    private fun makeExceptionModel(message: String, throwable: Throwable?): FIRExceptionModel {
        return FIRExceptionModel(name = "Logger", reason = message).apply {
            if (throwable != null) {
                // Copied from https://github.com/firebase/firebase-ios-sdk/issues/15512#issuecomment-3599554856
                stackTrace = throwable.getStackTrace().map { stackTraceElement ->
                    val elements = stackTraceElement.split("\\s+".toRegex())
                    FIRStackFrame.stackFrameWithAddress(elements[2].removePrefix("0x").toULong(16))
                }
            }
        }
    }
}
