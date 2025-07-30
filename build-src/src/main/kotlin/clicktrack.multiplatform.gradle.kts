import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    kotlin("multiplatform")
    id("clicktrack.android.library")
}

configure<KotlinMultiplatformExtension> {
    applyDefaultHierarchyTemplate()

    iosSimulatorArm64()
    androidTarget()
}
