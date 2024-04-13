package com.vsevolodganin.clicktrack

import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import com.vsevolodganin.clicktrack.utils.log.Logger
import me.tatarka.inject.annotations.Inject

@ApplicationScope
@Inject
class NativeLibraries(
    private val logger: Logger
) {
    fun init() {
        System.loadLibrary("oboe")
        System.loadLibrary("clicktrack")
        nativeSetGlobalLogger(logger)
    }
}

private external fun nativeSetGlobalLogger(logger: Logger)
