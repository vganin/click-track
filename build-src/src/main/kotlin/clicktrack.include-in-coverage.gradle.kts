plugins {
    id("org.jetbrains.kotlinx.kover")
}

koverReport {
    defaults {
        mergeWith("release")
    }
}

rootProject.dependencies {
    add("kover", project)
}
