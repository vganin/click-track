plugins {
    kotlin("multiplatform")
}

kotlin {
    ios()
    iosSimulatorArm64()

    sourceSets {
        val iosMain by getting
        val iosSimulatorArm64Main by getting

        iosSimulatorArm64Main.dependsOn(iosMain)
    }
}
