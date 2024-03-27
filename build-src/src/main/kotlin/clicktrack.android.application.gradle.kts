import com.android.build.gradle.AppExtension

plugins {
    id("com.android.application")
}

configure<AppExtension> {
    applyAndroidCommon()
}
