package com.vsevolodganin.clicktrack.primitiveaudio

import com.vsevolodganin.clicktrack.di.component.ApplicationScope
import com.vsevolodganin.clicktrack.model.ClickSoundSource
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(ApplicationScope::class)
@Inject
class PrimitiveAudioProvider(
    private val primitiveAudioExtractor: PrimitiveAudioExtractor,
) {
    fun get(sound: ClickSoundSource): PrimitiveFloatAudioData? {
        return primitiveAudioExtractor.extract(sound.uri, MAX_SECONDS)
            ?.let(PrimitiveFloatAudioData::from)
    }

    private companion object Const {
        const val MAX_SECONDS = 2
    }
}
