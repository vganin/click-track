package com.vsevolodganin.clicktrack.common

import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import com.vsevolodganin.clicktrack.BuildConfig as AndroidBuildConfig

@SingleIn(ApplicationScope::class)
@ContributesBinding(ApplicationScope::class)
@Inject
class BuildConfigImpl : BuildConfig {
    override val versionCode: Int get() = AndroidBuildConfig.VERSION_CODE
    override val versionName: String get() = AndroidBuildConfig.VERSION_NAME
}
