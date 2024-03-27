import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    kotlin("multiplatform")
}

configure<KotlinMultiplatformExtension> {
    iosArm64()
    iosSimulatorArm64()
    applyDefaultHierarchyTemplate()
}
