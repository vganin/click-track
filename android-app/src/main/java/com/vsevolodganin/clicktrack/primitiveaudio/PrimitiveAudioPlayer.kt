package com.vsevolodganin.clicktrack.primitiveaudio

interface PrimitiveAudioPlayer {
    fun prepare()
    fun release()
    fun loadAndGetIndex(data: PrimitiveAudioData): Int
    fun play(index: Int)
    fun stop(index: Int)
    fun getLatencyMs(): Int
}
