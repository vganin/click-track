plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    maven("https://plugins.gradle.org/m2/")
}

dependencies {
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlinx.kover.gradlePlugin)
    implementation(libs.ktlint.gradlePlugin)
}
