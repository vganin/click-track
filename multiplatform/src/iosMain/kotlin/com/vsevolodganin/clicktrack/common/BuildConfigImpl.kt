package com.vsevolodganin.clicktrack.common

import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import platform.Foundation.NSBundle

@SingleIn(ApplicationScope::class)
@ContributesBinding(ApplicationScope::class)
@Inject
class BuildConfigImpl : BuildConfig {
    override val versionCode: Int = 0
    override val versionName: String get() = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString").toString()
}
