import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    kotlin("multiplatform")
    id("clicktrack.android.library")
}

configure<KotlinMultiplatformExtension> {
    applyDefaultHierarchyTemplate()

    iosSimulatorArm64()

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            freeCompilerArgs.addAll("-P", "plugin:org.jetbrains.kotlin.parcelize:additionalAnnotation=com.vsevolodganin.clicktrack.utils.parcelable.Parcelize")
        }
    }

    jvmToolchain(17)

    // FIXME: To workaround https://github.com/icerockdev/moko-resources/issues/531
    sourceSets {
        val commonMain by getting
        androidMain {
            dependsOn(commonMain)
        }
    }
}
