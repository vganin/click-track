plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
}

kotlin {
    android()
    ios {
        iosSimulatorArm64()
    }

    cocoapods {
        version = "1.0"
        summary = "Click Track shared multiplatform code"
        homepage = "https://www.vsevolodganin.com/"
        ios.deploymentTarget = "13.5"
        name = "ClickTrackMultiplatform"

        framework {
            baseName = "ClickTrackMultiplatform"
        }
    }
}

android {
    compileSdk = 33
    namespace = "com.vsevolodganin.clicktrack.multiplatform"
}