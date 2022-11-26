plugins {
    kotlin("multiplatform")
    id("android-lib")
}

kotlin {
    android()
}

android {
    namespace = "com.vsevolodganin.clicktrack"
}
