plugins {
    id("clicktrack.multiplatform")
    id("clicktrack.include-in-coverage")
    id("clicktrack.ktlint")
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.metro)
    alias(libs.plugins.buildconfig)
    kotlin("native.cocoapods")
    kotlin("plugin.serialization")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.jetbrains.compose.runtime)
                implementation(libs.jetbrains.compose.foundation)
                implementation(libs.jetbrains.compose.ui)
                implementation(libs.jetbrains.compose.material3)
                implementation(libs.jetbrains.compose.materialIconsExtended)
                implementation(libs.jetbrains.compose.uiToolingPreview)
                implementation(libs.jetbrains.compose.resources)
                implementation(libs.simpleIcons)
                implementation(libs.bundles.decompose)
                implementation(libs.bundles.kotlinx.serialization)
                implementation(libs.kotlinx.datetime)
                implementation(libs.uuid)
                implementation(libs.metro.runtime)
                implementation(libs.sqldelight.runtime)
                implementation(libs.sqldelight.coroutines)
                implementation(libs.multiplatformSettings)
                implementation(libs.multiplatformSettings.coroutines)
                implementation(libs.reorderable)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        androidMain {
            dependencies {
                implementation(libs.androidx.appcompat)
                implementation(libs.androidx.annotation)
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.fragment)
                implementation(libs.androidx.constraintLayout.compose)
                implementation(libs.androidx.dataStore)
                implementation(libs.androidx.workManager)
                implementation(libs.androidx.splashScreen)
                implementation(libs.androidx.media)
                implementation(libs.firebase.bom.let { project.dependencies.platform(it) })
                implementation(libs.firebase.crashlytics)
                implementation(libs.googlePlay.review)
                implementation(libs.sqldelight.androidDriver)
            }
        }

        androidUnitTest {
            dependencies {
                implementation(libs.mockk.android)
                implementation(libs.mockk.agent)
                implementation(libs.testParameterInjector)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.sqldelight.nativeDriver)
            }
        }
    }

    cocoapods {
        name = "ClickTrackMultiplatform"
        version = "1.0"
        summary = "Shared multiplatform code"
        homepage = "https://www.vsevolodganin.com/"

        ios.deploymentTarget = "15.0"

        framework {
            baseName = "ClickTrackMultiplatform"
        }

        pod("FirebaseCrashlytics") {
            version = "12.8.0"
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

buildConfig {
    packageName("com.vsevolodganin.clicktrack.multiplatform")

    sourceSets.named("androidMain") {
        buildConfigField("Int", "VERSION_CODE", "${providers.gradleProperty("clicktrack.android.versionCode").get().toInt()}")
        buildConfigField("String", "VERSION_NAME", "\"${providers.gradleProperty("clicktrack.android.versionName").get()}\"")
    }

    sourceSets.named("androidDebug") {
        buildConfigField("Boolean", "DEBUG", "true")
    }

    sourceSets.named("androidRelease") {
        buildConfigField("Boolean", "DEBUG", "false")
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugarJdkLibs)
}
