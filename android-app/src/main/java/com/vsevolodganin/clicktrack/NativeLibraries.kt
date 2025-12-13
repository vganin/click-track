package com.vsevolodganin.clicktrack

import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(ApplicationScope::class)
@Inject
class NativeLibraries {
    fun init() {
        System.loadLibrary("clicktrack")
    }
}
