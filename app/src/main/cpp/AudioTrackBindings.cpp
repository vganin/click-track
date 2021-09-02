#include <jni.h>

#include "AudioTrack.h"

extern "C"
JNIEXPORT jlong JNICALL
Java_com_vsevolodganin_clicktrack_player_AudioTrack_initNative(
        JNIEnv* env,
        jobject thiz,
        jobject data,
        jint dataSize,
        jint channelCount,
        jint pcmEncoding,
        jint sampleRate
) {
    return (jlong) new clicktrack::AudioTrack(
            env->GetDirectBufferAddress(env->NewGlobalRef(data)), // TODO: Should release global ref at some point
            dataSize,
            channelCount,
            pcmEncoding,
            sampleRate
    );
}

extern "C"
JNIEXPORT void JNICALL
Java_com_vsevolodganin_clicktrack_player_AudioTrack_destroyNative(JNIEnv* env, jobject thiz, jlong native_ptr) {
    delete (clicktrack::AudioTrack*) (native_ptr);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_vsevolodganin_clicktrack_player_AudioTrack_warmup(JNIEnv* env, jobject thiz, jlong native_ptr) {
    auto audioTrack = (clicktrack::AudioTrack*) native_ptr;
    audioTrack->warmup();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_vsevolodganin_clicktrack_player_AudioTrack_play(JNIEnv* env, jobject thiz, jlong native_ptr) {
    auto audioTrack = (clicktrack::AudioTrack*) native_ptr;
    audioTrack->play();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_vsevolodganin_clicktrack_player_AudioTrack_stop(JNIEnv* env, jobject thiz, jlong native_ptr) {
    auto audioTrack = (clicktrack::AudioTrack*) native_ptr;
    audioTrack->stop();
}
