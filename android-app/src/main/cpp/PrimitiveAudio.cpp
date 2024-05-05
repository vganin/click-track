#include "PrimitiveAudio.h"

#include <stdexcept>
#include <string>

#include <resampler/MultiChannelResampler.h>

namespace {

using namespace RESAMPLER_OUTER_NAMESPACE::resampler;

class ResampleBlock {
public:
    int32_t sampleRate;
    const float* buffer;
    int32_t numSamples;
};

void resampleData_(const ResampleBlock& input, ResampleBlock* output, int numChannels) {
    // Calculate output buffer size
    double temp = ((double) input.numSamples * (double) output->sampleRate) / (double) input.sampleRate;

    // round up
    int32_t numOutFramesAllocated = (int32_t) (temp + 0.5);
    // We iterate thousands of times through the loop. Roundoff error could accumulate
    // so add a few more frames for padding
    numOutFramesAllocated += 8;

    MultiChannelResampler* resampler = MultiChannelResampler::make(
            numChannels,
            input.sampleRate,
            output->sampleRate,
            MultiChannelResampler::Quality::Medium
    );

    const float* inputBuffer = input.buffer; // multi-channel buffer to be consumed
    float* outputBuffer = new float[numOutFramesAllocated]; // multi-channel buffer to be filled
    output->buffer = outputBuffer;

    int numOutputSamples = 0;
    int inputSamplesLeft = input.numSamples;
    while ((inputSamplesLeft > 0) && (numOutputSamples < numOutFramesAllocated)) {
        if (resampler->isWriteNeeded()) {
            resampler->writeNextFrame(inputBuffer);
            inputBuffer += numChannels;
            inputSamplesLeft -= numChannels;
        } else {
            resampler->readNextFrame(outputBuffer);
            outputBuffer += numChannels;
            numOutputSamples += numChannels;
        }
    }
    output->numSamples = numOutputSamples;

    delete resampler;
}

} // namespace

PrimitiveAudio::PrimitiveAudio(
        const float* const samples,
        const int32_t samplesNumber,
        const int channelCount,
        const int32_t sampleRate
) : curSampleIndex_(0), isPlaying_(false), sampleData_(samples), numSamples_(samplesNumber) {
    audioProperties_.channelCount = channelCount;
    audioProperties_.sampleRate = sampleRate;
}

PrimitiveAudio::~PrimitiveAudio() {
    if (sampleData_ != nullptr) {
        delete[] sampleData_;
    }
}

void PrimitiveAudio::resampleData(int sampleRate) {
    if (audioProperties_.sampleRate == sampleRate) {
        // Nothing to do
        return;
    }

    ResampleBlock inputBlock;
    inputBlock.buffer = sampleData_;
    inputBlock.numSamples = numSamples_;
    inputBlock.sampleRate = audioProperties_.sampleRate;

    ResampleBlock outputBlock;
    outputBlock.sampleRate = sampleRate;
    ::resampleData_(inputBlock, &outputBlock, audioProperties_.channelCount);

    // Delete previous sampleData_
    delete[] sampleData_;

    // Install the resampled data
    sampleData_ = outputBlock.buffer;
    numSamples_ = outputBlock.numSamples;
    audioProperties_.sampleRate = outputBlock.sampleRate;
}

void PrimitiveAudio::mixAudio(float* outBuff, int numChannels, int32_t numFrames) {
    int32_t numSamples = numSamples_;
    int32_t sampleChannels = audioProperties_.channelCount;
    int32_t samplesLeft = numSamples - curSampleIndex_;
    int32_t numWriteFrames = isPlaying_ ? std::min(numFrames, samplesLeft / sampleChannels) : 0;

    if (numWriteFrames != 0) {
        const float* data = sampleData_;
        if ((sampleChannels == 1) && (numChannels == 1)) {
            // MONO output from MONO samples
            for (int32_t frameIndex = 0; frameIndex < numWriteFrames; frameIndex++) {
                outBuff[frameIndex] += data[curSampleIndex_++];
            }
        } else if ((sampleChannels == 1) && (numChannels == 2)) {
            // STEREO output from MONO samples
            int dstSampleIndex = 0;
            for (int32_t frameIndex = 0; frameIndex < numWriteFrames; frameIndex++) {
                outBuff[dstSampleIndex++] += data[curSampleIndex_];
                outBuff[dstSampleIndex++] += data[curSampleIndex_++];
            }
        } else if ((sampleChannels == 2) && (numChannels == 1)) {
            // MONO output from STEREO samples
            int dstSampleIndex = 0;
            for (int32_t frameIndex = 0; frameIndex < numWriteFrames; frameIndex++) {
                outBuff[dstSampleIndex] += data[curSampleIndex_++];
                outBuff[dstSampleIndex++] += data[curSampleIndex_++];
            }
        } else if ((sampleChannels == 2) && (numChannels == 2)) {
            // STEREO output from STEREO samples
            int dstSampleIndex = 0;
            for (int32_t frameIndex = 0; frameIndex < numWriteFrames; frameIndex++) {
                outBuff[dstSampleIndex++] += data[curSampleIndex_++];
                outBuff[dstSampleIndex++] += data[curSampleIndex_++];
            }
        }

        if (curSampleIndex_ >= numSamples) {
            isPlaying_ = false;
        }
    }

    // silence
    // no need as the output buffer would need to have been filled with silence
    // to be mixed into
}
