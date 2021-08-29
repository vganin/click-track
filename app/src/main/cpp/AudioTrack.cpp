#include "AudioTrack.h"

#include "Log.h"

#include <oboe/Oboe.h>
#include <media/NdkMediaFormat.h>

namespace clicktrack {

namespace {

void throwUnsupportedPcmEncoding(int pcmEncoding) {
    throw std::runtime_error("Unsupported PCM encoding: " + std::to_string(pcmEncoding));
}

oboe::AudioFormat pcmFormatToOboeFormat(int pcmEncoding) {
    switch (pcmEncoding) {
        case 2: // AudioFormat.ENCODING_PCM_16BIT
            return oboe::AudioFormat::I16;
        case 4: // AudioFormat.ENCODING_PCM_FLOAT
            return oboe::AudioFormat::Float;
        case 21: // AudioFormat.ENCODING_PCM_24BIT_PACKED
            return oboe::AudioFormat::I24;
        case 22: // AudioFormat.ENCODING_PCM_32BIT
            return oboe::AudioFormat::I32;
        default:
            throwUnsupportedPcmEncoding(pcmEncoding);
    }
    return oboe::AudioFormat::I16;
}

}

AudioTrack::AudioTrack(
        void* data,
        int32_t dataSize,
        int channelCount,
        int pcmEncoding,
        int32_t sampleRate
) : data_(data),
    playbackFrameIndex_(0) {
    auto oboeAudioFormat = pcmFormatToOboeFormat(pcmEncoding);

    auto result = oboe::AudioStreamBuilder()
            .setDirection(oboe::Direction::Output)
            ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
            ->setSharingMode(oboe::SharingMode::Shared)
            ->setSampleRate(sampleRate)
            ->setFormat(oboeAudioFormat)
            ->setChannelCount(channelCount)
            ->setUsage(oboe::Usage::Media)
            ->setContentType(oboe::ContentType::Sonification)
            ->setDataCallback(this)
            ->openStream(audioStream_);

    if (result != oboe::Result::OK) {
        LOGE("Failed to create stream. Error: %s", oboe::convertToText(result));
    }

    totalFrames_ = dataSize / audioStream_->getBytesPerFrame();
}

AudioTrack::~AudioTrack() {
    audioStream_->close();
}

oboe::DataCallbackResult AudioTrack::onAudioReady(oboe::AudioStream* audioStream, void* audioData, int32_t numFrames) {
    auto bytesPerFrame = audioStream->getBytesPerFrame();
    auto framesRemaining = totalFrames_ - playbackFrameIndex_;
    auto framesToPlay = std::min(framesRemaining, numFrames);
    auto framesToFillSilence = numFrames - framesToPlay;

    auto outputStart = static_cast<char*>(audioData);
    auto inputStart = static_cast<char*>(data_) + playbackFrameIndex_ * bytesPerFrame;

    memcpy(outputStart, inputStart, framesToPlay * bytesPerFrame);
    memset(outputStart + framesToPlay * bytesPerFrame, 0, framesToFillSilence * bytesPerFrame);

    playbackFrameIndex_ += framesToPlay;

    if (playbackFrameIndex_ == totalFrames_) {
        return oboe::DataCallbackResult::Stop;
    } else {
        return oboe::DataCallbackResult::Continue;
    }
}

void AudioTrack::play() {
    stop();
    audioStream_->start();
}

void AudioTrack::stop() {
    audioStream_->stop();
    playbackFrameIndex_ = 0;
}


}
