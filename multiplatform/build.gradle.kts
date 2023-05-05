@file:Suppress("UnstableApiUsage")

import org.jetbrains.compose.compose

@Suppress("DSL_SCOPE_VIOLATION") // FIXME(https://github.com/gradle/gradle/issues/22797)
plugins {
    id("clicktrack.multiplatform.android")
    id("clicktrack.multiplatform.ios")
    id("clicktrack.svg2compose")
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.ksp)
    kotlin("native.cocoapods")
    kotlin("plugin.serialization")
    kotlin("plugin.parcelize")
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
                optIn("kotlin.ExperimentalStdlibApi")
                optIn("kotlin.contracts.ExperimentalContracts")
                optIn("kotlin.time.ExperimentalTime")
                optIn("kotlinx.coroutines.DelicateCoroutinesApi")
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                optIn("kotlinx.coroutines.FlowPreview")
                optIn("kotlinx.coroutines.InternalCoroutinesApi")
                optIn("kotlinx.coroutines.ObsoleteCoroutinesApi")
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
            }

            kotlin.srcDir("build/generated/source/svg2compose")
        }

        androidMain {
            dependencies {
                api(compose.uiTooling)
                api(compose.preview)
                // FIXME: Shouldn't need below
                api("androidx.compose.ui:ui-tooling-preview:1.4.2")
                api("androidx.compose.ui:ui-tooling:1.4.2")
                api(libs.androidx.activity.compose)
                api(libs.androidx.constraintLayout.compose)
                api(libs.bundles.accompanist)
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
            isStatic = true
        }

        podfile = rootProject.file("ios-app/Podfile")
    }
}

android {
    namespace = "com.vsevolodganin.clicktrack.multiplatform"

    externalNativeBuild {
        cmake {
            path = file("src/androidMain/cpp/CMakeLists.txt")
        }
    }

    defaultConfig {
        externalNativeBuild {
            cmake {
                arguments("-DANDROID_STL=c++_shared")
            }
        }
    }

    packagingOptions {
        jniLibs {
            excludes += "**/libc++_shared.so"
        }
    }

    buildFeatures {
        prefabPublishing = true
    }

    prefab {
        create("multiplatform") {

        }
    }
}

multiplatformResources {
    disableStaticFrameworkWarning = true
}

// FIXME(https://github.com/google/ksp/issues/567): Improve KSP declarations
dependencies {
    for (configName in arrayOf(
        "kspCommonMainMetadata",
        "kspAndroid",
        "kspIosX64",
        "kspIosArm64",
        "kspIosSimulatorArm64"
    )) {
        add(configName, libs.kotlininject.compiler)
    }
}
