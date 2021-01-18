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
