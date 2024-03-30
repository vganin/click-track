#ifndef CLICKTRACK_PRIMITIVEAUDIO_H
#define CLICKTRACK_PRIMITIVEAUDIO_H

#include <stdint.h>

struct AudioProperties {
    int32_t channelCount;
    int32_t sampleRate;
};

class PrimitiveAudio {
public:
    /** @param encoding should be the same as `PrimitiveAudioData.Encoding.nativeIndex` */
    explicit PrimitiveAudio(
            const void* const bytes,
            const int32_t length,
            const int channelCount,
            const int encoding,
            const int32_t sampleRate
    );

    ~PrimitiveAudio();

    void resampleData(int sampleRate);

    void mixAudio(float* outBuff, int numChannels, int32_t numFrames);

    void setPlayMode() {
        curSampleIndex_ = 0;
        isPlaying_ = true;
    }

    void setStopMode() {
        isPlaying_ = false;
        curSampleIndex_ = 0;
    }

    bool isPlaying() { return isPlaying_; }

private:
    AudioProperties audioProperties_;

    float* sampleData_;
    int32_t numSamples_;

    int32_t curSampleIndex_;
    bool isPlaying_;
};


#endif //CLICKTRACK_PRIMITIVEAUDIO_H
