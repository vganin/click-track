import com.android.build.gradle.LibraryExtension

plugins {
    id("com.android.library")
}

configure<LibraryExtension> {
    applyAndroidCommon()
}
