package com.vsevolodganin.clicktrack.model

import ClickTrack.multiplatform.MR
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
            strongBeat = ClickSoundSource.Bundled(MR.files.beep_strong),
            weakBeat = ClickSoundSource.Bundled(MR.files.beep_weak),
        )
    ),
    CLAVES(
        nameResource = MR.strings.sound_library_claves,
        storageKey = "claves",
        sounds = GenericClickSounds(
            strongBeat = ClickSoundSource.Bundled(MR.files.claves_strong),
            weakBeat = ClickSoundSource.Bundled(MR.files.claves_weak),
        )
    ),
    COWBELL(
        nameResource = MR.strings.sound_library_cowbell,
        storageKey = "cowbell",
        sounds = GenericClickSounds(
            strongBeat = ClickSoundSource.Bundled(MR.files.cowbell_strong),
            weakBeat = ClickSoundSource.Bundled(MR.files.cowbell_weak),
        )
    ),
    DRUMS(
        nameResource = MR.strings.sound_library_drum_kit,
        storageKey = "drum kit",
        sounds = GenericClickSounds(
            strongBeat = ClickSoundSource.Bundled(MR.files.drumkit_base),
            weakBeat = ClickSoundSource.Bundled(MR.files.drumkit_hat),
        )
    ),
}
