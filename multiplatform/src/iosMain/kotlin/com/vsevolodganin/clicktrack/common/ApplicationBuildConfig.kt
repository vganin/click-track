package com.vsevolodganin.clicktrack.common

import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import platform.Foundation.NSBundle
import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalNativeApi::class)
@SingleIn(ApplicationScope::class)
@Inject
actual class ApplicationBuildConfig {
    actual val versionCode: Int = 0
    actual val versionName: String = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString").toString()
    actual val isDebug: Boolean = Platform.isDebugBinary
}
