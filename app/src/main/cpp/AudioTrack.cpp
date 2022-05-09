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
) : data_(data),
    dataSize_(dataSize),
    channelCount_(channelCount),
    pcmEncoding_(pcmEncoding),
    sampleRate_(sampleRate),
    playbackFrameIndex_(0),
    mute_(false),
    shouldResetStream_(false),
    audioStream_() {
    internalInit();
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

void AudioTrack::onErrorAfterClose(oboe::AudioStream* stream, oboe::Result error) {
    LOGE("Error occurred: %s", convertToText(error));
    if (error == oboe::Result::ErrorDisconnected) {
        LOGE("Restarting audio stream after disconnect");
        shouldResetStream_ = true;
    }
}

void AudioTrack::resetStream() {
    audioStream_->stop();
    audioStream_->close();
    internalInit();
}

void AudioTrack::warmup() {
    internalResetStreamIfNeeded();

    // Play silence for warmup
    mute_ = true;
    audioStream_->requestStart();
}

void AudioTrack::play() {
    internalResetStreamIfNeeded();

    mute_ = false;
    playbackFrameIndex_ = 0;
    const auto state = audioStream_->getState();

    using oboe::StreamState;
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

void AudioTrack::internalResetStreamIfNeeded() {
    if (shouldResetStream_) {
        shouldResetStream_ = false;
        internalInit();
    }
}

void AudioTrack::internalInit() {
    const auto oboeAudioFormat = pcmFormatToOboeFormat(pcmEncoding_);

    const auto result = oboe::AudioStreamBuilder()
            .setDirection(oboe::Direction::Output)
            ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
            ->setSharingMode(oboe::SharingMode::Shared)
            ->setSampleRate(sampleRate_)
            ->setFormat(oboeAudioFormat)
            ->setChannelCount(channelCount_)
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
    totalFrames_ = dataSize_ / bytesPerFrame_;
}

}
