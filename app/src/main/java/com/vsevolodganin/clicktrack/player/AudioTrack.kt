package com.vsevolodganin.clicktrack.player

import java.nio.ByteBuffer

class AudioTrack(
    data: ByteBuffer,
    channelCount: Int,
    pcmEncoding: Int,
    sampleRate: Int,
) {
    init {
        require(data.isDirect) { "Only direct buffers are accepted" }
    }

    private val nativePtr: Long = initNative(data, data.position(), channelCount, pcmEncoding, sampleRate)

    fun warmup() = warmup(nativePtr)
    fun resetStream() = resetStream(nativePtr)
    fun play() = play(nativePtr)
    fun stop() = stop(nativePtr)
    fun getLatencyMs() = getLatencyMs(nativePtr)

    private external fun initNative(data: ByteBuffer, dataSize: Int, channelCount: Int, pcmFormat: Int, sampleRate: Int): Long
    private external fun destroyNative(nativePtr: Long)
    private external fun resetStream(nativePtr: Long)
    private external fun warmup(nativePtr: Long)
    private external fun play(nativePtr: Long)
    private external fun stop(nativePtr: Long)
    private external fun getLatencyMs(nativePtr: Long): Long

    protected fun finalize() {
        destroyNative(nativePtr)
    }
}
