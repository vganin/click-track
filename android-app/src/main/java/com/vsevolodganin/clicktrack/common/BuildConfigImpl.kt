package com.vsevolodganin.clicktrack.common

import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import me.tatarka.inject.annotations.Inject
import com.vsevolodganin.clicktrack.BuildConfig as AndroidBuildConfig

@Inject
@ApplicationScope
class BuildConfigImpl : BuildConfig {
    override val versionCode: Int get() = AndroidBuildConfig.VERSION_CODE
    override val versionName: String get() = AndroidBuildConfig.VERSION_NAME
}
