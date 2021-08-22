-keepattributes SourceFile, LineNumberTable

### { KotlinX Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt # core serialization annotations
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.vsevolodganin.**$$serializer { *; }
-keepclassmembers class com.vsevolodganin.** {
    *** Companion;
}
-keepclasseswithmembers class com.vsevolodganin.** {
    kotlinx.serialization.KSerializer serializer(...);
}
### }

### { FIXME: Try to workaround `Verifier rejected class com.google.android.gms.measurement.internal.AppMeasurementDynamiteService`
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**
###

### Keep action class names for analytics logging
-keepnames class com.vsevolodganin.clicktrack.state.redux.core.Action
-keepnames class ** implements com.vsevolodganin.clicktrack.state.redux.core.Action
###
