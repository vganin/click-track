plugins {
    `ios-multiplatform`
    kotlin("native.cocoapods")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":multiplatform"))
                api(project(":compose-ui"))
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

            export(project(":multiplatform"))
            export(project(":compose-ui"))
        }

        podfile = rootProject.file("ios-app/Podfile")
    }
}
