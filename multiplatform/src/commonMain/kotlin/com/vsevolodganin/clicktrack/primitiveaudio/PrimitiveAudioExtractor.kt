package com.vsevolodganin.clicktrack.primitiveaudio

expect class PrimitiveAudioExtractor {
    fun extract(uri: String, maxSeconds: Int): PrimitiveAudioData?
}
