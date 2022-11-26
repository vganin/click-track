plugins {
    kotlin("multiplatform")
}

kotlin {
    ios()
    iosSimulatorArm64()

    sourceSets["iosSimulatorArm64Main"].dependsOn(sourceSets["iosMain"])
}
