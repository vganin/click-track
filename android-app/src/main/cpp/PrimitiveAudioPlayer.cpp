#include "PrimitiveAudioPlayer.h"

#include <android/log.h>

namespace {

using namespace oboe;

static const char* TAG = "PrimitiveAudioPlayer";

} // namespace

DataCallbackResult PrimitiveAudioPlayer::OboeDataCallback::onAudioReady(
        AudioStream* oboeStream,
        void* audioData,
        int32_t numFrames
) {
    StreamState streamState = oboeStream->getState();
    if (streamState != StreamState::Open && streamState != StreamState::Started) {
        parent_->logger_->logError(
                TAG,
                "onAudioReady: streamState: " + std::string{convertToText(streamState)}
        );
    }

    memset(audioData, 0, static_cast<size_t>(numFrames) * static_cast<size_t>(parent_->channelCount_) * sizeof(float));

    for (int32_t index = 0; index < parent_->numPrimitiveAudios_; index++) {
        if (parent_->primitiveAudios_[index]->isPlaying()) {
            parent_->primitiveAudios_[index]->mixAudio((float*) audioData, parent_->channelCount_, numFrames);
        }
    }

    return DataCallbackResult::Continue;
}

void PrimitiveAudioPlayer::OboeErrorCallback::onErrorAfterClose(
        AudioStream* oboeStream,
        Result error
) {
    parent_->logger_->logError(
            TAG,
            "onErrorAfterClose: " + std::string{convertToText(error)}
    );

    parent_->openStream();
    parent_->startStream();
}

PrimitiveAudioPlayer::PrimitiveAudioPlayer()
        : logger_(nullptr),
          channelCount_(0),
          sampleRate_(0),
          numPrimitiveAudios_(0) {}

PrimitiveAudioPlayer::~PrimitiveAudioPlayer() {
    release();
    setLogger(nullptr);
}

void PrimitiveAudioPlayer::setLogger(Logger* logger) {
    __android_log_print(ANDROID_LOG_INFO, TAG, "setLogger");

    if (logger_ != nullptr) {
        delete logger_;
    }

    logger_ = logger;
}

void PrimitiveAudioPlayer::prepare() {
    __android_log_print(ANDROID_LOG_INFO, TAG, "prepare");

    channelCount_ = 2; // TODO: Could be better than this?
    openStream();
    startStream();
}

void PrimitiveAudioPlayer::release() {
    __android_log_print(ANDROID_LOG_INFO, TAG, "release");

    if (audioStream_) {
        audioStream_->stop();
        audioStream_->close();
        audioStream_.reset();
    }

    for (size_t bufferIndex = 0; bufferIndex < numPrimitiveAudios_; bufferIndex++) {
        delete primitiveAudios_[bufferIndex];
    }

    primitiveAudios_.clear();
    numPrimitiveAudios_ = 0;
}

int PrimitiveAudioPlayer::loadAndGetIndex(
        const void* const bytes,
        const int32_t length,
        const int channelCount,
        const int encoding,
        const int32_t sampleRate
) {
    __android_log_print(ANDROID_LOG_INFO, TAG, "loadAndGetIndex");

    const auto audio = new PrimitiveAudio(
            bytes,
            length,
            channelCount,
            encoding,
            sampleRate
    );

    audio->resampleData(sampleRate_);

    primitiveAudios_.push_back(audio);

    return numPrimitiveAudios_++;
}

void PrimitiveAudioPlayer::play(int index) {
    __android_log_print(ANDROID_LOG_INFO, TAG, "play");

    if (index >= 0 && index < numPrimitiveAudios_) {
        primitiveAudios_[index]->setPlayMode();
    } else {
        logger_->logError(
                TAG,
                "Wrong index passed: " + std::to_string(index) + ". Source num was " + std::to_string(numPrimitiveAudios_)
        );
    }
}

void PrimitiveAudioPlayer::stop(int index) {
    __android_log_print(ANDROID_LOG_INFO, TAG, "stop");

    if (index >= 0 && index < numPrimitiveAudios_) {
        primitiveAudios_[index]->setStopMode();
    } else {
        logger_->logError(
                TAG,
                "Wrong index passed: " + std::to_string(index) + ". Source num was " + std::to_string(numPrimitiveAudios_)
        );
    }
}

int PrimitiveAudioPlayer::getLatencyMs() {
    __android_log_print(ANDROID_LOG_INFO, TAG, "getLatencyMs");

    if (audioStream_) {
        const auto result = audioStream_->calculateLatencyMillis();
        if (result != Result::OK) {
            logger_->logError(
                    TAG,
                    "calculateLatencyMillis failed. Error: " + std::string{convertToText(result.error())}
            );
            return result.value();
        } else {
            return 0;
        }
    } else {
        return 0;
    }
}

bool PrimitiveAudioPlayer::openStream() {
    __android_log_print(ANDROID_LOG_INFO, TAG, "openStream");

    dataCallback_ = std::make_shared<OboeDataCallback>(this);
    errorCallback_ = std::make_shared<OboeErrorCallback>(this);

    AudioStreamBuilder builder;
    builder.setChannelCount(channelCount_);
    builder.setFormat(AudioFormat::Float);
    builder.setDataCallback(dataCallback_);
    builder.setErrorCallback(errorCallback_);
    builder.setPerformanceMode(PerformanceMode::LowLatency);
    builder.setSharingMode(SharingMode::Exclusive);
    builder.setSampleRateConversionQuality(SampleRateConversionQuality::Medium);

    Result result = builder.openStream(audioStream_);
    if (result != Result::OK) {
        logger_->logError(
                TAG,
                "openStream failed. Error: " + std::string{convertToText(result)}
        );
        return false;
    }

    // Reduce stream latency by setting the buffer size to a multiple of the burst size
    // Note: this will fail with ErrorUnimplemented if we are using a callback with OpenSL ES
    // See oboe::AudioStreamBuffered::setBufferSizeInFrames
    static constexpr int32_t bufferSizeInBursts = 2; // Use 2 bursts as the buffer size (double buffer)
    result = audioStream_->setBufferSizeInFrames(audioStream_->getFramesPerBurst() * bufferSizeInBursts);
    if (result != Result::OK) {
        logger_->logError(
                TAG,
                "setBufferSizeInFrames failed. Error: " + std::string{convertToText(result)}
        );
    }

    sampleRate_ = audioStream_->getSampleRate();

    return true;
}

bool PrimitiveAudioPlayer::startStream() {
    __android_log_print(ANDROID_LOG_INFO, TAG, "startStream");

    int tryCount = 0;
    while (tryCount < 3) {
        bool wasOpenSuccessful = true;
        // Assume that openStream() was called successfully before startStream() call
        if (tryCount > 0) {
            usleep(20 * 1000); // Sleep between tries to give the system time to settle
            wasOpenSuccessful = openStream(); // Try to open the stream again after the first try
        }
        if (wasOpenSuccessful) {
            Result result = audioStream_->requestStart();
            if (result != Result::OK) {
                logger_->logError(
                        TAG,
                        "requestStart failed. Error: " + std::string{convertToText(result)}
                );
                audioStream_->close();
                audioStream_.reset();
            } else {
                return true;
            }
        }
        tryCount++;
    }

    return false;
}
