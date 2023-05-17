package com.vsevolodganin.clicktrack.settings.debug

import com.vsevolodganin.clicktrack.utils.native.nativeCrash
import me.tatarka.inject.annotations.Inject

@Inject
class NativeCrashImpl : NativeCrash {
    override operator fun invoke() = nativeCrash()
}
