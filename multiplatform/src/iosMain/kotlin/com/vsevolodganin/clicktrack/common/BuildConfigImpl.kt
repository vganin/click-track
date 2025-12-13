package com.vsevolodganin.clicktrack.common

import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import platform.Foundation.NSBundle

@Inject
@SingleIn(ApplicationScope::class)
class BuildConfigImpl : BuildConfig {
    override val versionCode: Int = 0
    override val versionName: String get() = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString").toString()
}
