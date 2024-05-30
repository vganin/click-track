package com.vsevolodganin.clicktrack.settings.debug

import me.tatarka.inject.annotations.Inject
import platform.Foundation.NSException
import platform.Foundation.raise

@Inject
class NativeCrashImpl : NativeCrash {
    override fun exception() = NSException.raise("Test", "Test")

    override fun danglingReference() = TODO("Unimplemented")
}
