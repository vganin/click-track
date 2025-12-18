package com.vsevolodganin.clicktrack.common

import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import com.vsevolodganin.clicktrack.multiplatform.AndroidMainBuildConfig
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(ApplicationScope::class)
@Inject
actual class ApplicationBuildConfig {
    actual val versionCode: Int get() = AndroidMainBuildConfig.VERSION_CODE
    actual val versionName: String get() = AndroidMainBuildConfig.VERSION_NAME
    actual val isDebug: Boolean get() = AndroidMainBuildConfig.DEBUG
}
