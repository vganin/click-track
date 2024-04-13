#include <jni.h>

#include "PrimitiveAudioPlayer.h"

namespace {

static PrimitiveAudioPlayer player;

} // namespace

extern "C" {

JNIEXPORT void JNICALL
Java_com_vsevolodganin_clicktrack_primitiveaudio_PrimitiveAudioPlayerImplKt_nativePrepare(
        JNIEnv* env,
        jclass clazz
) {
    player.prepare();
}

JNIEXPORT void JNICALL
Java_com_vsevolodganin_clicktrack_primitiveaudio_PrimitiveAudioPlayerImplKt_nativeRelease(
        JNIEnv* env,
        jclass clazz
) {
    player.release();
}

JNIEXPORT jint JNICALL
Java_com_vsevolodganin_clicktrack_primitiveaudio_PrimitiveAudioPlayerImplKt_nativeLoadAndGetIndex(
        JNIEnv* env,
        jclass clazz,
        jbyteArray bytes,
        jint length,
        jint encoding_index,
        jint sample_rate,
        jint channel_count
) {
    auto bytesPtr = env->GetPrimitiveArrayCritical(bytes, 0);

    const auto index = player.loadAndGetIndex(
            bytesPtr,
            length,
            channel_count,
            encoding_index,
            sample_rate
    );

    env->ReleasePrimitiveArrayCritical(bytes, bytesPtr, 0);

    return index;
}

JNIEXPORT void JNICALL
Java_com_vsevolodganin_clicktrack_primitiveaudio_PrimitiveAudioPlayerImplKt_nativePlay(
        JNIEnv* env,
        jclass clazz,
        jint index
) {
    player.play(index);
}

JNIEXPORT void JNICALL
Java_com_vsevolodganin_clicktrack_primitiveaudio_PrimitiveAudioPlayerImplKt_nativeStop(
        JNIEnv* env,
        jclass clazz,
        jint index
) {
    player.stop(index);
}

JNIEXPORT jint JNICALL
Java_com_vsevolodganin_clicktrack_primitiveaudio_PrimitiveAudioPlayerImplKt_nativeGetLatencyMs(
        JNIEnv* env,
        jclass clazz
) {
    return player.getLatencyMs();
}

JNIEXPORT void JNICALL
Java_com_vsevolodganin_clicktrack_NativeLibrariesKt_nativeSetGlobalLogger(
        JNIEnv* env,
        jclass clazz,
        jobject logger
) {
    player.setLogger(new Logger{env, logger});
}

} // extern "C"
