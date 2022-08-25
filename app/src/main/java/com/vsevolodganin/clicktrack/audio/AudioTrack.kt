package com.vsevolodganin.clicktrack.audio

class AudioTrack(
    data: ByteArray,
    pcmEncoding: Int,
    channelCount: Int,
    sampleRate: Int,
) {
    private val nativePtr: Long = initNative(data, data.size, channelCount, pcmEncoding, sampleRate)

    fun warmup() = warmup(nativePtr)
    fun resetStream() = resetStream(nativePtr)
    fun play() = play(nativePtr)
    fun stop() = stop(nativePtr)
    fun getLatencyMs() = getLatencyMs(nativePtr)

    private external fun initNative(data: ByteArray, dataSize: Int, channelCount: Int, pcmFormat: Int, sampleRate: Int): Long
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
