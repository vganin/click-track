rootProject.name = "ClickTrack"

includeBuild("build-src")

include(":android-app")
include(":multiplatform")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
