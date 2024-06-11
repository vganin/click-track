package com.vsevolodganin.clicktrack.model

import com.vsevolodganin.clicktrack.generated.resources.MR
import dev.icerock.moko.resources.StringResource

enum class BuiltinClickSounds(
    val nameResource: StringResource,
    val storageKey: String,
    val sounds: ClickSounds,
) {
    BEEP(
        nameResource = MR.strings.sound_library_beep,
        storageKey = "beep",
        sounds = GenericClickSounds(
            strongBeat = ClickSoundSource.Bundled(MR.files.beep_strong_wav),
            weakBeat = ClickSoundSource.Bundled(MR.files.beep_weak_wav),
        ),
    ),
    CLAVES(
        nameResource = MR.strings.sound_library_claves,
        storageKey = "claves",
        sounds = GenericClickSounds(
            strongBeat = ClickSoundSource.Bundled(MR.files.claves_strong_wav),
            weakBeat = ClickSoundSource.Bundled(MR.files.claves_weak_wav),
        ),
    ),
    COWBELL(
        nameResource = MR.strings.sound_library_cowbell,
        storageKey = "cowbell",
        sounds = GenericClickSounds(
            strongBeat = ClickSoundSource.Bundled(MR.files.cowbell_strong_wav),
            weakBeat = ClickSoundSource.Bundled(MR.files.cowbell_weak_wav),
        ),
    ),
    DRUMS(
        nameResource = MR.strings.sound_library_drum_kit,
        storageKey = "drum kit",
        sounds = GenericClickSounds(
            strongBeat = ClickSoundSource.Bundled(MR.files.drumkit_base_wav),
            weakBeat = ClickSoundSource.Bundled(MR.files.drumkit_hat_wav),
        ),
    ),
}
