package com.vsevolodganin.clicktrack.model

import org.jetbrains.compose.resources.StringResource
import clicktrack.multiplatform.composeresources.generated.resources.Res
import com.vsevolodganin.clicktrack.generated.resources.MR

enum class BuiltinClickSounds(
    val nameResource: StringResource,
    val storageKey: String,
    val sounds: ClickSounds,
) {
    BEEP(
        nameResource = Res.string.sound_library_beep,
        storageKey = "beep",
        sounds = GenericClickSounds(
            strongBeat = ClickSoundSource.Bundled(MR.files.beep_strong_wav),
            weakBeat = ClickSoundSource.Bundled(MR.files.beep_weak_wav),
        ),
    ),
    CLAVES(
        nameResource = Res.string.sound_library_claves,
        storageKey = "claves",
        sounds = GenericClickSounds(
            strongBeat = ClickSoundSource.Bundled(MR.files.claves_strong_wav),
            weakBeat = ClickSoundSource.Bundled(MR.files.claves_weak_wav),
        ),
    ),
    COWBELL(
        nameResource = Res.string.sound_library_cowbell,
        storageKey = "cowbell",
        sounds = GenericClickSounds(
            strongBeat = ClickSoundSource.Bundled(MR.files.cowbell_strong_wav),
            weakBeat = ClickSoundSource.Bundled(MR.files.cowbell_weak_wav),
        ),
    ),
    DRUMS(
        nameResource = Res.string.sound_library_drum_kit,
        storageKey = "drum kit",
        sounds = GenericClickSounds(
            strongBeat = ClickSoundSource.Bundled(MR.files.drumkit_base_wav),
            weakBeat = ClickSoundSource.Bundled(MR.files.drumkit_hat_wav),
        ),
    ),
}
