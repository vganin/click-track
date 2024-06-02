@file:Suppress("UnstableApiUsage")

import org.jetbrains.compose.compose

@Suppress("DSL_SCOPE_VIOLATION") // FIXME(https://github.com/gradle/gradle/issues/22797)
plugins {
    id("clicktrack.multiplatform")
    id("clicktrack.include-in-coverage")
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.ksp)
    kotlin("native.cocoapods")
    kotlin("plugin.serialization")
    id("dev.icerock.mobile.multiplatform-resources")
}

kotlin {
    sourceSets {
        all {
            languageSettings {
                optIn("androidx.compose.animation.ExperimentalAnimationApi")
                optIn("androidx.compose.animation.core.InternalAnimationApi")
                optIn("androidx.compose.foundation.ExperimentalFoundationApi")
                optIn("androidx.compose.material.ExperimentalMaterialApi")
                optIn("androidx.compose.ui.ExperimentalComposeUiApi")
                optIn("androidx.compose.ui.text.ExperimentalTextApi")
                optIn("com.russhwolf.settings.ExperimentalSettingsApi")
                optIn("kotlin.ExperimentalStdlibApi")
                optIn("kotlin.contracts.ExperimentalContracts")
                optIn("kotlin.time.ExperimentalTime")
                optIn("kotlinx.coroutines.DelicateCoroutinesApi")
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                optIn("kotlinx.coroutines.FlowPreview")
                optIn("kotlinx.coroutines.InternalCoroutinesApi")
                optIn("kotlinx.coroutines.ObsoleteCoroutinesApi")
                optIn("kotlin.experimental.ExperimentalNativeApi")
            }
        }

        commonMain {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.ui)
                api(compose.material)
                api(compose.materialIconsExtended)
                api(compose("org.jetbrains.compose.ui:ui-util")) // FIXME(https://github.com/JetBrains/compose-jb/issues/1295): Replace with `org.jetbrains.compose.ComposePlugin.Dependencies` field when it's available
                api(libs.simpleIcons)
                api(libs.bundles.decompose)
                api(libs.bundles.kotlinx.serialization)
                api(libs.kotlinx.datetime)
                api(libs.moko.resources)
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

multiplatformResources {
    resourcesPackage.set("com.vsevolodganin.clicktrack.generated.resources")
    resourcesClassName.set("MR")
}

sqldelight {
    databases {
        create("Database") {
            packageName = "com.vsevolodganin.clicktrack"
            schemaOutputDirectory = file("src/commonMain/sqldelight/schema")
        }
    }
}

// FIXME(https://github.com/google/ksp/issues/567): Improve KSP declarations
dependencies {
    coreLibraryDesugaring(libs.desugarJdkLibs)

    with(libs.kotlininject.compiler) {
        kspCommonMainMetadata(this)
        kspAndroid(this)
        kspIosSimulatorArm64(this)
    }
}
