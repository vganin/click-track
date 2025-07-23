import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion

fun BaseExtension.applyAndroidCommon() {
    compileSdkVersion(36)

    defaultConfig {
        minSdk = 23
        targetSdk = 36
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}
