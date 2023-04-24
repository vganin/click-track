package com.vsevolodganin.clicktrack.utils.platform

import com.vsevolodganin.clicktrack.multiplatform.BuildConfig

actual fun isDebug(): Boolean = BuildConfig.DEBUG
