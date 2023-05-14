package com.vsevolodganin.clicktrack.common

import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import me.tatarka.inject.annotations.Inject
import platform.Foundation.NSBundle

@Inject
@ApplicationScope
class BuildConfigImpl : BuildConfig {
    override val versionCode: Int = 0
    override val displayVersion: String get() = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString").toString()
}
