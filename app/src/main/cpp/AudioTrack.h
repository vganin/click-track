#ifndef CLICKTRACK_AUDIOTRACK_H
#define CLICKTRACK_AUDIOTRACK_H

#include <oboe/Oboe.h>

namespace clicktrack {

class AudioTrack : public oboe::AudioStreamDataCallback, public oboe::AudioStreamErrorCallback {
public:
    explicit AudioTrack(
            void* data,
            int32_t dataSize,
            int channelCount,
            int pcmEncoding,
            int32_t sampleRate
    );

    ~AudioTrack();

    oboe::DataCallbackResult onAudioReady(oboe::AudioStream* audioStream, void* audioData, int32_t numFrames) override;

    bool onError(oboe::AudioStream* audioStream, oboe::Result error) override;

    void warmup();

    void play();

    void stop();

private:
    void* data_;
    int32_t totalFrames_;
    std::shared_ptr<oboe::AudioStream> audioStream_;
    std::atomic<int32_t> playbackFrameIndex_;
};

}

#endif //CLICKTRACK_AUDIOTRACK_H
