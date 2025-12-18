package com.vsevolodganin.clicktrack.common

expect class ApplicationBuildConfig {
    val versionCode: Int
    val versionName: String
    val isDebug: Boolean
}
