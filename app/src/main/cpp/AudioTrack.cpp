#include "AudioTrack.h"

#include "Log.h"

#include <oboe/Oboe.h>
#include <media/NdkMediaFormat.h>
#include <cmath>
#include <cstdlib>

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
        const void* const data,
        const int32_t dataSize,
        const int channelCount,
        const int pcmEncoding,
        const int32_t sampleRate
) : data_(data),
    dataSize_(dataSize),
    channelCount_(channelCount),
    pcmEncoding_(pcmEncoding),
    sampleRate_(sampleRate),
    mutex(),
    playbackFrameIndex_(0),
    mute_(false),
    isPlaying(false),
    audioStream_(),
    totalFrames_(0),
    bytesPerFrame_(0) {
    createStream();
}

AudioTrack::~AudioTrack() {
    disposeStream();
    std::free(const_cast<void*>(data_));
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

bool AudioTrack::onError(oboe::AudioStream* stream, oboe::Result error) {
    LOGE("Error occurred: %s", convertToText(error));
    if (error == oboe::Result::ErrorDisconnected) {
        const std::lock_guard<std::mutex> lock{mutex};
        recoverStream();
        return true;
    }
    return false;
}

void AudioTrack::recover() {
    const std::lock_guard<std::mutex> lock{mutex};
    recoverStream();
}

void AudioTrack::warmup() {
    const std::lock_guard<std::mutex> lock{mutex};
    mute_ = true; // Play silence for warmup
    if (!isPlaying) {
        audioStream_->requestStart();
        isPlaying = true;
    }
}

void AudioTrack::play() {
    const std::lock_guard<std::mutex> lock{mutex};
    mute_ = false;
    playbackFrameIndex_ = 0;
    if (!isPlaying) {
        audioStream_->requestStart();
        isPlaying = true;
    }
}

void AudioTrack::stop() {
    const std::lock_guard<std::mutex> lock{mutex};
    if (isPlaying) {
        audioStream_->requestStop();
        isPlaying = false;
    }
}

int64_t AudioTrack::getLatencyMs() {
    const std::lock_guard<std::mutex> lock{mutex};
    const auto result = audioStream_->calculateLatencyMillis();
    if (result) {
        return static_cast<int64_t>(std::round(result.value()));
    } else {
        LOGE("Failed to calculate latency: %s", oboe::convertToText(result.error()));
        return 0;
    }
}

void AudioTrack::createStream() {
    const auto oboeAudioFormat = pcmFormatToOboeFormat(pcmEncoding_);

    const auto result = oboe::AudioStreamBuilder()
            .setDirection(oboe::Direction::Output)
            ->setPerformanceMode(oboe::PerformanceMode::LowLatency)
            ->setSharingMode(oboe::SharingMode::Shared)
            ->setSampleRate(sampleRate_)
            ->setSampleRateConversionQuality(oboe::SampleRateConversionQuality::Fastest)
            ->setFormat(oboeAudioFormat)
            ->setFormatConversionAllowed(true)
            ->setChannelCount(channelCount_)
            ->setChannelConversionAllowed(true)
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

void AudioTrack::disposeStream() {
    audioStream_->stop();
    audioStream_->close();
}

void AudioTrack::recoverStream() {
    disposeStream();
    createStream();
    if (isPlaying) {
        audioStream_->requestStart();
    }
}

}
