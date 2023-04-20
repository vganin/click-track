plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation(libs.gradlePlugins.kotlin)
    implementation(libs.gradlePlugins.android)
    implementation(libs.svg2compose)
}
