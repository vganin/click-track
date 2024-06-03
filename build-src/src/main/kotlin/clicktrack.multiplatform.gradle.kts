import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    kotlin("multiplatform")
    id("clicktrack.android.library")
}

configure<KotlinMultiplatformExtension> {
    applyDefaultHierarchyTemplate()

    iosSimulatorArm64()
    androidTarget()

    jvmToolchain(17)

    // FIXME: To workaround https://github.com/icerockdev/moko-resources/issues/531
    sourceSets {
        val commonMain by getting
        androidMain {
            dependsOn(commonMain)
        }
    }
}
