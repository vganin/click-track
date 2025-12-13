package com.vsevolodganin.clicktrack.settings.debug

import com.vsevolodganin.clicktrack.utils.native.nativeDanglingReferenceCrash
import com.vsevolodganin.clicktrack.utils.native.nativeExceptionCrash
import dev.zacsweers.metro.Inject

@Inject
class NativeCrashImpl : NativeCrash {
    override fun exception() = nativeExceptionCrash()

    override fun danglingReference() = nativeDanglingReferenceCrash()
}
