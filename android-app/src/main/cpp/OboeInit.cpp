#include <jni.h>

#include <oboe/Oboe.h>

extern "C"
JNIEXPORT void JNICALL
Java_com_vsevolodganin_clicktrack_NativeLibraries_initOboeDefaults(
        JNIEnv* env,
        jobject thiz,
        jint defaultSampleRate,
        jint defaultFramesPerBurst
) {
    oboe::DefaultStreamValues::SampleRate = (int32_t) defaultSampleRate;
    oboe::DefaultStreamValues::FramesPerBurst = (int32_t) defaultFramesPerBurst;
}
