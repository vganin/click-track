plugins {
    `android-multiplatform`
    `ios-multiplatform`
    alias(libs.plugins.jetbrains.compose)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
            }
        }
    }
}

android {
    namespace = "com.vsevolodganin.clicktrack.compose.ui"
}
