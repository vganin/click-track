import com.android.build.gradle.BaseExtension

fun BaseExtension.applyAndroidCommon() {
    compileSdkVersion(34)

    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }
}
