package com.vsevolodganin.clicktrack.primitiveaudio

import com.vsevolodganin.clicktrack.di.component.PlayerServiceScope
import me.tatarka.inject.annotations.Inject

@PlayerServiceScope
@Inject
class PrimitiveAudioPlayerImpl : PrimitiveAudioPlayer {

    override fun prepare() = nativePrepare()

    override fun release() = nativeRelease()

    override fun loadAndGetIndex(data: PrimitiveAudioData): Int =
        data.run { nativeLoadAndGetIndex(bytes, bytes.size, encoding.nativeIndex, sampleRate, channelCount) }

    override fun play(index: Int) = nativePlay(index)

    override fun stop(index: Int) = nativeStop(index)

    override fun getLatencyMs(): Int = nativeGetLatencyMs()
}

private external fun nativePrepare()
private external fun nativeRelease()
private external fun nativeLoadAndGetIndex(bytes: ByteArray, length: Int, encodingIndex: Int, sampleRate: Int, channelCount: Int): Int
private external fun nativePlay(index: Int)
private external fun nativeStop(index: Int)
private external fun nativeGetLatencyMs(): Int
