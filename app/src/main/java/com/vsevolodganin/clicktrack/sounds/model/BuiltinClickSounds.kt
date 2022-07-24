package com.vsevolodganin.clicktrack.sounds.model

import androidx.annotation.StringRes
import com.vsevolodganin.clicktrack.R

enum class BuiltinClickSounds(
    @StringRes val nameStringRes: Int,
    val storageKey: String,
    val sounds: ClickSounds,
) {
    BEEP(
        nameStringRes = R.string.builtin_sounds_beep,
        storageKey = "beep",
        sounds = ClickSounds(
            strongBeat = ClickSoundSource.Bundled(R.raw.beep_strong),
            weakBeat = ClickSoundSource.Bundled(R.raw.beep_weak),
        )
    ),
    CLAVES(
        nameStringRes = R.string.builtin_sounds_claves,
        storageKey = "claves",
        sounds = ClickSounds(
            strongBeat = ClickSoundSource.Bundled(R.raw.claves_strong),
            weakBeat = ClickSoundSource.Bundled(R.raw.claves_weak),
        )
    ),
    COWBELL(
        nameStringRes = R.string.builtin_sounds_cowbell,
        storageKey = "cowbell",
        sounds = ClickSounds(
            strongBeat = ClickSoundSource.Bundled(R.raw.cowbell_strong),
            weakBeat = ClickSoundSource.Bundled(R.raw.cowbell_weak),
        )
    ),
    DRUMS(
        nameStringRes = R.string.builtin_sounds_drum_kit,
        storageKey = "drum kit",
        sounds = ClickSounds(
            strongBeat = ClickSoundSource.Bundled(R.raw.drumkit_base),
            weakBeat = ClickSoundSource.Bundled(R.raw.drumkit_hat),
        )
    ),
}
