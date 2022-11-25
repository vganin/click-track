package com.vsevolodganin.clicktrack.model

import androidx.annotation.StringRes
import com.vsevolodganin.clicktrack.R

enum class BuiltinClickSounds(
    @StringRes val nameStringRes: Int,
    val storageKey: String,
    val sounds: ClickSounds,
) {
    BEEP(
        nameStringRes = R.string.sound_library_beep,
        storageKey = "beep",
        sounds = GenericClickSounds(
            strongBeat = ClickSoundSource.Bundled(R.raw.beep_strong),
            weakBeat = ClickSoundSource.Bundled(R.raw.beep_weak),
        )
    ),
    CLAVES(
        nameStringRes = R.string.sound_library_claves,
        storageKey = "claves",
        sounds = GenericClickSounds(
            strongBeat = ClickSoundSource.Bundled(R.raw.claves_strong),
            weakBeat = ClickSoundSource.Bundled(R.raw.claves_weak),
        )
    ),
    COWBELL(
        nameStringRes = R.string.sound_library_cowbell,
        storageKey = "cowbell",
        sounds = GenericClickSounds(
            strongBeat = ClickSoundSource.Bundled(R.raw.cowbell_strong),
            weakBeat = ClickSoundSource.Bundled(R.raw.cowbell_weak),
        )
    ),
    DRUMS(
        nameStringRes = R.string.sound_library_drum_kit,
        storageKey = "drum kit",
        sounds = GenericClickSounds(
            strongBeat = ClickSoundSource.Bundled(R.raw.drumkit_base),
            weakBeat = ClickSoundSource.Bundled(R.raw.drumkit_hat),
        )
    ),
}
