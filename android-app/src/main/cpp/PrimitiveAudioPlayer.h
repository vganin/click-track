#ifndef CLICKTRACK_PRIMITIVEAUDIOPLAYER_H
#define CLICKTRACK_PRIMITIVEAUDIOPLAYER_H

#include <oboe/Oboe.h>
#include <stdint.h>
#include <vector>

#include "PrimitiveAudio.h"

class PrimitiveAudioPlayer {
public:
    explicit PrimitiveAudioPlayer();

    ~PrimitiveAudioPlayer();

    void prepare();

    void release();

    int loadAndGetIndex(
            const void* const bytes,
            const int32_t length,
            const int channelCount,
            const int encoding,
            const int32_t sampleRate
    );

    void play(int index);

    void stop(int index);

    int getLatencyMs();

private:
    class OboeDataCallback : public oboe::AudioStreamDataCallback {
    public:
        OboeDataCallback(PrimitiveAudioPlayer* parent) : parent_(parent) {}

        oboe::DataCallbackResult onAudioReady(
                oboe::AudioStream* audioStream,
                void* audioData,
                int32_t numFrames
        ) override;

    private:
        PrimitiveAudioPlayer* parent_;
    };

    class OboeErrorCallback : public oboe::AudioStreamErrorCallback {
    public:
        OboeErrorCallback(PrimitiveAudioPlayer* parent) : parent_(parent) {}

        virtual ~OboeErrorCallback() {}

        void onErrorAfterClose(
                oboe::AudioStream* oboeStream,
                oboe::Result error
        ) override;

    private:
        PrimitiveAudioPlayer* parent_;
    };

    bool openStream();
    bool startStream();

    std::shared_ptr<oboe::AudioStream> audioStream_;

    int32_t channelCount_;
    int32_t sampleRate_;

    int32_t numPrimitiveAudios_;
    std::vector<PrimitiveAudio*> primitiveAudios_;

    std::shared_ptr<OboeDataCallback> dataCallback_;
    std::shared_ptr<OboeErrorCallback> errorCallback_;
};

#endif //CLICKTRACK_PRIMITIVEAUDIOPLAYER_H
