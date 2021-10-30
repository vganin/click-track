#ifndef CLICKTRACK_AUDIOTRACK_H
#define CLICKTRACK_AUDIOTRACK_H

#include <oboe/Oboe.h>

namespace clicktrack {

class AudioTrack : public oboe::AudioStreamDataCallback, public oboe::AudioStreamErrorCallback {
public:
    explicit AudioTrack(
            const void* data,
            const int32_t dataSize,
            const int channelCount,
            const int pcmEncoding,
            const int32_t sampleRate
    );

    ~AudioTrack();

    oboe::DataCallbackResult onAudioReady(oboe::AudioStream* audioStream, void* audioData, int32_t numFrames) override;

    bool onError(oboe::AudioStream* audioStream, oboe::Result error) override;

    void warmup();

    void play();

    void stop();

private:
    const void* const data_;

    std::atomic<int32_t> playbackFrameIndex_;
    std::atomic<bool> mute_;

    std::shared_ptr<oboe::AudioStream> audioStream_;
    int32_t totalFrames_;
    int32_t bytesPerFrame_;
};

}

#endif //CLICKTRACK_AUDIOTRACK_H
