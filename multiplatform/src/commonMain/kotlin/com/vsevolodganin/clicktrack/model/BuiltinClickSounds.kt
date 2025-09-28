package com.vsevolodganin.clicktrack.model

import clicktrack.multiplatform.generated.resources.Res
import clicktrack.multiplatform.generated.resources.sound_library_beep
import clicktrack.multiplatform.generated.resources.sound_library_claves
import clicktrack.multiplatform.generated.resources.sound_library_cowbell
import clicktrack.multiplatform.generated.resources.sound_library_drum_kit
import org.jetbrains.compose.resources.StringResource

enum class BuiltinClickSounds(
    val nameResource: StringResource,
    val storageKey: String,
    val sounds: ClickSounds,
) {
    BEEP(
        nameResource = Res.string.sound_library_beep,
        storageKey = "beep",
        sounds = ClickSounds(
            strongBeat = ClickSoundSource(Res.getUri("files/beep_strong.wav")),
            weakBeat = ClickSoundSource(Res.getUri("files/beep_weak.wav")),
        ),
    ),
    CLAVES(
        nameResource = Res.string.sound_library_claves,
        storageKey = "claves",
        sounds = ClickSounds(
            strongBeat = ClickSoundSource(Res.getUri("files/claves_strong.wav")),
            weakBeat = ClickSoundSource(Res.getUri("files/claves_weak.wav")),
        ),
    ),
    COWBELL(
        nameResource = Res.string.sound_library_cowbell,
        storageKey = "cowbell",
        sounds = ClickSounds(
            strongBeat = ClickSoundSource(Res.getUri("files/cowbell_strong.wav")),
            weakBeat = ClickSoundSource(Res.getUri("files/cowbell_weak.wav")),
        ),
    ),
    DRUMS(
        nameResource = Res.string.sound_library_drum_kit,
        storageKey = "drum kit",
        sounds = ClickSounds(
            strongBeat = ClickSoundSource(Res.getUri("files/drumkit_base.wav")),
            weakBeat = ClickSoundSource(Res.getUri("files/drumkit_hat.wav")),
        ),
    ),
}
