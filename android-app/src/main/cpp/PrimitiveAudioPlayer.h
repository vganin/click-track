#ifndef CLICKTRACK_PRIMITIVEAUDIOPLAYER_H
#define CLICKTRACK_PRIMITIVEAUDIOPLAYER_H

#include <oboe/Oboe.h>
#include <stdint.h>
#include <vector>

#include "PrimitiveAudio.h"
#include "Logger.h"

class PrimitiveAudioPlayer {
public:
    explicit PrimitiveAudioPlayer();

    ~PrimitiveAudioPlayer();

    void setLogger(Logger* logger);

    void prepare();

    void release();

    int loadAndGetIndex(
            const float* const samples,
            const int32_t samplesNumber,
            const int channelCount,
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

    Logger* logger_;

    std::shared_ptr<oboe::AudioStream> audioStream_;

    int32_t channelCount_;
    int32_t sampleRate_;

    int32_t numPrimitiveAudios_;
    std::vector<PrimitiveAudio*> primitiveAudios_;

    std::shared_ptr<OboeDataCallback> dataCallback_;
    std::shared_ptr<OboeErrorCallback> errorCallback_;
};

#endif //CLICKTRACK_PRIMITIVEAUDIOPLAYER_H
