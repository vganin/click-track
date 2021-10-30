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
        const void* data,
        const int32_t dataSize,
        const int channelCount,
        const int pcmEncoding,
        const int32_t sampleRate
) : audioStream_(),
    data_(data),
    playbackFrameIndex_(0),
    mute_(false) {

    const auto oboeAudioFormat = pcmFormatToOboeFormat(pcmEncoding);

    const auto result = oboe::AudioStreamBuilder()
            .setDirection(oboe::Direction::Output)
            ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
            ->setSharingMode(oboe::SharingMode::Shared)
            ->setSampleRate(sampleRate)
            ->setFormat(oboeAudioFormat)
            ->setChannelCount(channelCount)
            ->setUsage(oboe::Usage::Media)
            ->setContentType(oboe::ContentType::Sonification)
            ->setDataCallback(this)
            ->setErrorCallback(this)
            ->openStream(audioStream_);

    if (result != oboe::Result::OK) {
        LOGE("Failed to open stream: %s", oboe::convertToText(result));
        return;
    }

    bytesPerFrame_ = audioStream_->getBytesPerFrame();
    totalFrames_ = dataSize / bytesPerFrame_;
}

AudioTrack::~AudioTrack() {
    audioStream_->close();
}

oboe::DataCallbackResult AudioTrack::onAudioReady(oboe::AudioStream* audioStream, void* audioData, int32_t numFrames) {
    // Filling with silence to avoid glitches
    memset(audioData, 0, numFrames * bytesPerFrame_);

    if (!mute_) {
        const auto playbackFrameIndex = playbackFrameIndex_.fetch_add(numFrames);
        const auto framesRemaining = std::max(0, totalFrames_ - playbackFrameIndex);
        const auto framesToPlay = std::min(framesRemaining, numFrames);
        const auto inputStart = static_cast<const char*>(data_) + playbackFrameIndex * bytesPerFrame_;
        memcpy(audioData, inputStart, framesToPlay * bytesPerFrame_);
    }

    return oboe::DataCallbackResult::Continue;
}

bool AudioTrack::onError(oboe::AudioStream* audioStream, oboe::Result error) {
    LOGE("Error occurred: %s", convertToText(error));
    return false;
}

void AudioTrack::warmup() {
    // Play silence for warmup
    mute_ = true;
    audioStream_->requestStart();
}

void AudioTrack::play() {
    using oboe::StreamState;

    mute_ = false;
    playbackFrameIndex_ = 0;
    const auto state = audioStream_->getState();
    if (state != StreamState::Starting && state != StreamState::Started) {
        audioStream_->requestStart();
    }
}

void AudioTrack::stop() {
    using oboe::StreamState;

    const auto state = audioStream_->getState();
    if (state != StreamState::Stopping && state != StreamState::Stopped) {
        audioStream_->requestStop();
    }
}

}
