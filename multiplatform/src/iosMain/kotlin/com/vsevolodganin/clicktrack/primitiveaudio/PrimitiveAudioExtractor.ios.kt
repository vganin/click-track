package com.vsevolodganin.clicktrack.primitiveaudio

import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import com.vsevolodganin.clicktrack.utils.log.Logger
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.interpretCPointer
import kotlinx.cinterop.objcPtr
import kotlinx.cinterop.pointed
import kotlinx.cinterop.usePinned
import platform.AVFAudio.AVFormatIDKey
import platform.AVFAudio.AVLinearPCMBitDepthKey
import platform.AVFAudio.AVLinearPCMIsBigEndianKey
import platform.AVFAudio.AVLinearPCMIsFloatKey
import platform.AVFoundation.AVAssetReader
import platform.AVFoundation.AVAssetReaderTrackOutput
import platform.AVFoundation.AVAssetTrack
import platform.AVFoundation.AVMediaTypeAudio
import platform.AVFoundation.AVURLAsset
import platform.AVFoundation.AVURLAssetPreferPreciseDurationAndTimingKey
import platform.AVFoundation.formatDescriptions
import platform.AVFoundation.tracksWithMediaType
import platform.CoreAudioTypes.kAudioFormatLinearPCM
import platform.CoreMedia.CMAudioFormatDescriptionGetStreamBasicDescription
import platform.CoreMedia.CMBlockBufferCopyDataBytes
import platform.CoreMedia.CMBlockBufferGetDataLength
import platform.CoreMedia.CMSampleBufferGetDataBuffer
import platform.Foundation.NSURL

@SingleIn(ApplicationScope::class)
@Inject
actual class PrimitiveAudioExtractor(
    private val logger: Logger,
) {

    @OptIn(ExperimentalForeignApi::class)
    actual fun extract(
        uri: String,
        maxSeconds: Int,
    ): PrimitiveAudioData? {
        return try {
            val url = NSURL.URLWithString(uri)
                ?: run {
                    logger.logError(TAG, "Failed to create NSURL from $uri")
                    return null
                }

            val asset = AVURLAsset(
                uRL = url,
                options = mapOf(AVURLAssetPreferPreciseDurationAndTimingKey to true),
            )

            val audioTrack = asset.tracksWithMediaType(AVMediaTypeAudio)
                .asSequence()
                .filterIsInstance<AVAssetTrack>()
                .firstOrNull()
                ?: run {
                    logger.logError(TAG, "Failed to find audio track in $uri")
                    return null
                }

            val assetReader = AVAssetReader(
                asset = asset,
                error = null,
            )

            val assetReaderOutput = AVAssetReaderTrackOutput(
                track = audioTrack,
                outputSettings = mapOf(
                    // Required
                    AVFormatIDKey to kAudioFormatLinearPCM,

                    // Forcing encoding to be `PCM_FLOAT_32BIT_LITTLE_ENDIAN`
                    AVLinearPCMIsFloatKey to true,
                    AVLinearPCMBitDepthKey to 32,
                    AVLinearPCMIsBigEndianKey to false,
                ),
            ).apply {
                // We don't modify output in-place
                alwaysCopiesSampleData = false
            }

            val audioTrackDescription = audioTrack.formatDescriptions.firstOrNull()
                ?.let { interpretCPointer<cnames.structs.opaqueCMFormatDescription>(it.objcPtr()) }
                ?.let { CMAudioFormatDescriptionGetStreamBasicDescription(it) }
                ?: run {
                    logger.logError(TAG, "Failed to get audio track description for $uri")
                    return null
                }

            val sampleRate = audioTrackDescription.pointed.mSampleRate.toInt()
            val channelCount = audioTrackDescription.pointed.mChannelsPerFrame.toInt()
            val outputMaxBytes = maxSeconds * sampleRate * channelCount * OUTPUT_ENCODING.bytesPerSample
            val outputBuffer = ByteArray(outputMaxBytes)
            var totalBytesRead = 0

            assetReader.addOutput(assetReaderOutput)
            if (!assetReader.startReading()) {
                logger.logError(TAG, "Failed to start reading $uri")
                return null
            }

            while (true) {
                val sampleBuffer = assetReaderOutput.copyNextSampleBuffer() ?: break
                val dataBuffer = CMSampleBufferGetDataBuffer(sampleBuffer) ?: break
                val dataLength = CMBlockBufferGetDataLength(dataBuffer).toInt()
                val remaining = outputMaxBytes - totalBytesRead
                val toCopy = minOf(remaining, dataLength)

                if (toCopy <= 0) break

                outputBuffer.usePinned { pinnedOutputBuffer ->
                    CMBlockBufferCopyDataBytes(
                        theSourceBuffer = dataBuffer,
                        offsetToData = 0UL,
                        dataLength = toCopy.toULong(),
                        destination = pinnedOutputBuffer.addressOf(totalBytesRead),
                    )
                }

                totalBytesRead += toCopy

                if (toCopy < dataLength) break
            }

            if (totalBytesRead <= 0) {
                logger.logError(TAG, "Read 0 bytes from the stream $uri")
                return null
            }

            PrimitiveAudioData(
                bytes = outputBuffer.copyOf(totalBytesRead),
                encoding = OUTPUT_ENCODING,
                sampleRate = sampleRate,
                channelCount = channelCount,
            )
        } catch (t: Throwable) {
            logger.logError(TAG, "Failed to extract PCM", t)
            null
        }
    }

    private companion object {
        const val TAG = "PrimitiveAudioExtractor"
        val OUTPUT_ENCODING = PrimitiveAudioData.Encoding.PCM_FLOAT_32BIT_LITTLE_ENDIAN
    }
}
