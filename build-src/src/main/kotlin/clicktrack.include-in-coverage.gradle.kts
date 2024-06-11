plugins {
    id("org.jetbrains.kotlinx.kover")
}

rootProject.dependencies {
    add("kover", project)
}
