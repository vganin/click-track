#ifndef CLICKTRACK_AUDIOTRACK_H
#define CLICKTRACK_AUDIOTRACK_H

#include <oboe/Oboe.h>

namespace clicktrack {

class AudioTrack : public oboe::AudioStreamDataCallback, public oboe::AudioStreamErrorCallback {
public:
    explicit AudioTrack(
            const void* const data,
            const int32_t dataSize,
            const int channelCount,
            const int pcmEncoding,
            const int32_t sampleRate
    );

    ~AudioTrack();

    oboe::DataCallbackResult onAudioReady(oboe::AudioStream* audioStream, void* audioData, int32_t numFrames) override;

    bool onError(oboe::AudioStream* stream, oboe::Result result) override;

    void recover();

    void warmup();

    void play();

    void stop();

    int64_t getLatencyMs();

private:
    const void* const data_;
    const int32_t dataSize_;
    const int channelCount_;
    const int pcmEncoding_;
    const int32_t sampleRate_;

    std::mutex mutex;

    std::atomic<int32_t> playbackFrameIndex_;
    std::atomic<bool> mute_;

    bool isPlaying;

    std::shared_ptr<oboe::AudioStream> audioStream_;
    int32_t totalFrames_;
    int32_t bytesPerFrame_;

    void createStream();

    void disposeStream();

    void recoverStream();
};

}

#endif //CLICKTRACK_AUDIOTRACK_H
