import org.jetbrains.compose.compose

plugins {
    `android-multiplatform`
    `ios-multiplatform`
    alias(libs.plugins.jetbrains.compose)
    kotlin("native.cocoapods")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("kotlin-parcelize")
    id("dev.icerock.mobile.multiplatform-resources")
}

kotlin {
    sourceSets {
        all {
            languageSettings {
                optIn("kotlin.ExperimentalStdlibApi")
                optIn("androidx.compose.ui.text.ExperimentalTextApi")
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
                implementation(libs.mokoResources)
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
}

multiplatformResources {
    disableStaticFrameworkWarning = true
}
