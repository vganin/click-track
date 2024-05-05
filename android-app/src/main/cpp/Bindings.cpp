#include <jni.h>

#include "PrimitiveAudioPlayer.h"

namespace {

static PrimitiveAudioPlayer player;

} // namespace

extern "C"
JNIEXPORT void JNICALL
Java_com_vsevolodganin_clicktrack_primitiveaudio_PrimitiveAudioPlayerImplKt_nativePrepare(
        JNIEnv* env,
        jclass clazz
) {
    player.prepare();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_vsevolodganin_clicktrack_primitiveaudio_PrimitiveAudioPlayerImplKt_nativeRelease(
        JNIEnv* env,
        jclass clazz
) {
    player.release();
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_vsevolodganin_clicktrack_primitiveaudio_PrimitiveAudioPlayerImplKt_nativeLoadAndGetIndex(
        JNIEnv* env,
        jclass clazz,
        jfloatArray samples,
        jint samplesNumber,
        jint sample_rate,
        jint channel_count
) {
    auto samplesPtr = env->GetFloatArrayElements(samples, 0);

    auto samplesCopy = new float[samplesNumber];

    std::memcpy(samplesCopy, samplesPtr, samplesNumber * sizeof(float));

    env->ReleaseFloatArrayElements(samples, samplesPtr, 0);

    const auto index = player.loadAndGetIndex(
            samplesCopy,
            samplesNumber,
            channel_count,
            sample_rate
    );

    return index;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_vsevolodganin_clicktrack_primitiveaudio_PrimitiveAudioPlayerImplKt_nativePlay(
        JNIEnv* env,
        jclass clazz,
        jint index
) {
    player.play(index);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_vsevolodganin_clicktrack_primitiveaudio_PrimitiveAudioPlayerImplKt_nativeStop(
        JNIEnv* env,
        jclass clazz,
        jint index
) {
    player.stop(index);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_vsevolodganin_clicktrack_primitiveaudio_PrimitiveAudioPlayerImplKt_nativeGetLatencyMs(
        JNIEnv* env,
        jclass clazz
) {
    return player.getLatencyMs();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_vsevolodganin_clicktrack_NativeLibrariesKt_nativeSetGlobalLogger(
        JNIEnv* env,
        jclass clazz,
        jobject logger
) {
    player.setLogger(new Logger{env, logger});
}
