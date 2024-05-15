#include <jni.h>

#include <resampler/MultiChannelResampler.h>

using namespace oboe::resampler;

extern "C"
JNIEXPORT jlong JNICALL
Java_com_vsevolodganin_clicktrack_primitiveaudio_Resampler_createNative(
        JNIEnv* env,
        jobject thiz,
        jint channelCount,
        jint inputRate,
        jint outputRate,
        jint quality
) {
    return (jlong) MultiChannelResampler::make(
            channelCount,
            inputRate,
            outputRate,
            static_cast<MultiChannelResampler::Quality>(quality)
    );
}

extern "C"
JNIEXPORT jfloatArray JNICALL
Java_com_vsevolodganin_clicktrack_primitiveaudio_Resampler_resampleNative(
        JNIEnv* env,
        jobject thiz,
        jlong ptr,
        jfloatArray inputJavaSamples,
        jint channelCount,
        jint inputRate,
        jint outputRate
) {
    const auto resampler = (MultiChannelResampler*) ptr;
    const auto samplesNumber = env->GetArrayLength(inputJavaSamples);

    const auto inputSamples = env->GetFloatArrayElements(inputJavaSamples, NULL);

    // Calculate output buffer size
    double temp = ((double) samplesNumber * (double) outputRate) / (double) inputRate;
    // Round up
    int32_t numOutFramesAllocated = (int32_t) (temp + 0.5);
    // We iterate thousands of times through the loop. Roundoff error could accumulate
    // so add a few more frames for padding
    numOutFramesAllocated += 8;

    jfloatArray outputJavaSamples = env->NewFloatArray(numOutFramesAllocated);
    const auto outputSamples = env->GetFloatArrayElements(outputJavaSamples, NULL);

    {
        int numOutputSamples = 0;
        int inputSamplesLeft = samplesNumber;
        auto inputBuffer = inputSamples;
        auto outputBuffer = outputSamples;
        while ((inputSamplesLeft > 0) && (numOutputSamples < numOutFramesAllocated)) {
            if (resampler->isWriteNeeded()) {
                resampler->writeNextFrame(inputBuffer);
                inputBuffer += channelCount;
                inputSamplesLeft -= channelCount;
            } else {
                resampler->readNextFrame(outputBuffer);
                outputBuffer += channelCount;
                numOutputSamples += channelCount;
            }
        }
    }

    env->ReleaseFloatArrayElements(inputJavaSamples, inputSamples, 0);
    env->ReleaseFloatArrayElements(outputJavaSamples, outputSamples, 0);

    return outputJavaSamples;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_vsevolodganin_clicktrack_primitiveaudio_Resampler_destroyNative(
        JNIEnv* env,
        jobject thiz,
        jlong ptr
) {
    const auto resampler = (MultiChannelResampler*) ptr;
    delete resampler;
}
