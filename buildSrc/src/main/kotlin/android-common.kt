import com.android.build.gradle.BaseExtension

fun BaseExtension.applyAndroidCommon() {
    compileSdkVersion(33)

    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }
}
