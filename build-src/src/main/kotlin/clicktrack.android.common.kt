import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion

fun BaseExtension.applyAndroidCommon() {
    compileSdkVersion(34)

    defaultConfig {
        minSdk = 21
        targetSdk = 34
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
