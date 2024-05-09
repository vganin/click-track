package com.vsevolodganin.clicktrack

import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import me.tatarka.inject.annotations.Inject

@ApplicationScope
@Inject
class NativeLibraries {
    fun init() {
        System.loadLibrary("clicktrack")
    }
}
