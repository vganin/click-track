plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.android.library")
    id("kotlin-parcelize")
}

kotlin {
    android()

    sourceSets {
        all {
            languageSettings {
                optIn("kotlin.ExperimentalMultiplatform")
                optIn("kotlin.time.ExperimentalTime")
                optIn("kotlinx.serialization.ExperimentalSerializationApi")
            }
        }
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:${libs.versions.kotlin.serialization.get()}")
            }
        }
    }

    explicitApi()
}

android {
    compileSdk = 31
    sourceSets["main"].setRoot("src/androidMain")
}
