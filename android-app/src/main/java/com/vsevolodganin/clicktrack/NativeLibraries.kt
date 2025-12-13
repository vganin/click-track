package com.vsevolodganin.clicktrack

import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import dev.zacsweers.metro.Inject

@ApplicationScope
@Inject
class NativeLibraries {
    fun init() {
        System.loadLibrary("clicktrack")
    }
}
