#include <jni.h>

#include "AudioTrack.h"

#include <cstdlib>

extern "C"
JNIEXPORT jlong JNICALL
Java_com_vsevolodganin_clicktrack_audio_AudioTrack_initNative(
        JNIEnv* env,
        jobject thiz,
        jbyteArray data,
        jint dataSize,
        jint channelCount,
        jint pcmEncoding,
        jint sampleRate
) {
    void* dataCopy = std::malloc(dataSize);
    env->GetByteArrayRegion(data, 0, dataSize, static_cast<jbyte*>(dataCopy));

    return (jlong) new clicktrack::AudioTrack(
            dataCopy,
            dataSize,
            channelCount,
            pcmEncoding,
            sampleRate
    );
}

extern "C"
JNIEXPORT void JNICALL
Java_com_vsevolodganin_clicktrack_audio_AudioTrack_destroyNative(JNIEnv* env, jobject thiz, jlong native_ptr) {
    delete (clicktrack::AudioTrack*) (native_ptr);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_vsevolodganin_clicktrack_audio_AudioTrack_resetStream(JNIEnv* env, jobject thiz, jlong native_ptr) {
    auto audioTrack = (clicktrack::AudioTrack*) native_ptr;
    audioTrack->resetStream();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_vsevolodganin_clicktrack_audio_AudioTrack_warmup(JNIEnv* env, jobject thiz, jlong native_ptr) {
    auto audioTrack = (clicktrack::AudioTrack*) native_ptr;
    audioTrack->warmup();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_vsevolodganin_clicktrack_audio_AudioTrack_play(JNIEnv* env, jobject thiz, jlong native_ptr) {
    auto audioTrack = (clicktrack::AudioTrack*) native_ptr;
    audioTrack->play();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_vsevolodganin_clicktrack_audio_AudioTrack_stop(JNIEnv* env, jobject thiz, jlong native_ptr) {
    auto audioTrack = (clicktrack::AudioTrack*) native_ptr;
    audioTrack->stop();
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_vsevolodganin_clicktrack_audio_AudioTrack_getLatencyMs(JNIEnv* env, jobject thiz, jlong native_ptr) {
    auto audioTrack = (clicktrack::AudioTrack*) native_ptr;
    return audioTrack->getLatencyMs();
}
