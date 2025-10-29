@file:Suppress("UnstableApiUsage")

import org.jetbrains.compose.compose

plugins {
    id("clicktrack.multiplatform")
    id("clicktrack.include-in-coverage")
    id("clicktrack.ktlint")
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.ksp)
    kotlin("native.cocoapods")
    kotlin("plugin.serialization")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.ui)
                api(compose.material)
                api(compose.materialIconsExtended)
                api(compose.components.uiToolingPreview)
                // FIXME(https://github.com/JetBrains/compose-jb/issues/1295): Replace with `org.jetbrains.compose.ComposePlugin.Dependencies` field when it's available
                api(compose("org.jetbrains.compose.ui:ui-util"))
                api(compose.components.resources)
                api(libs.simpleIcons)
                api(libs.bundles.decompose)
                api(libs.bundles.kotlinx.serialization)
                api(libs.kotlinx.datetime)
                api(libs.uuid)
                api(libs.kotlininject.runtime)
                api(libs.sqldelight.runtime)
                api(libs.sqldelight.coroutines)
                api(libs.multiplatformSettings)
                api(libs.multiplatformSettings.coroutines)
                api(libs.reorderable)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        androidMain {
            dependencies {
                api(compose.uiTooling)
                api(compose.preview)
                api(libs.androidx.annotation)
                api(libs.androidx.activity.compose)
                api(libs.androidx.constraintLayout.compose)
                api(libs.androidx.dataStore)
                api(libs.bundles.accompanist)
                api(libs.sqldelight.androidDriver)
            }
        }

        androidUnitTest {
            dependencies {
                implementation(libs.mockk.android)
                implementation(libs.mockk.agent)
            }
        }

        iosMain {
            dependencies {
                api(libs.sqldelight.nativeDriver)
            }
        }
    }

    cocoapods {
        name = "ClickTrackMultiplatform"
        version = "1.0"
        summary = "Shared multiplatform code"
        homepage = "https://www.vsevolodganin.com/"

        ios.deploymentTarget = "13.5"

        framework {
            baseName = "ClickTrackMultiplatform"
        }

        podfile = rootProject.file("ios-app/Podfile")
    }
}

android {
    namespace = "com.vsevolodganin.clicktrack.multiplatform"

    buildFeatures {
        compose = true
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName = "com.vsevolodganin.clicktrack"
            schemaOutputDirectory = file("src/commonMain/sqldelight/schema")
        }
    }
}

compose.resources {
    publicResClass = true
}

// FIXME(https://github.com/google/ksp/issues/567): Improve KSP declarations
dependencies {
    coreLibraryDesugaring(libs.desugarJdkLibs)

    with(libs.kotlininject.compiler) {
        kspCommonMainMetadata(this)
        kspAndroid(this)
        kspIosX64(this)
        kspIosArm64(this)
        kspIosSimulatorArm64(this)
    }
}
