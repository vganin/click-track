package com.vsevolodganin.clicktrack.utils.platform

import platform.Foundation.NSException
import platform.Foundation.raise

actual fun nativeCrash() {
    NSException.raise("Test", "Test")
}
