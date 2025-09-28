package com.vsevolodganin.clicktrack.model

enum class BuiltinClickSounds(
    val nameResourceId: String, // Changed to String
    val storageKey: String,
    val sounds: ClickSounds,
) {
    BEEP(
        nameResourceId = "sound_library_beep",
        storageKey = "beep",
        sounds = GenericClickSounds(
            strongBeat = ClickSoundSource.Bundled("files/beep_strong.wav"),
            weakBeat = ClickSoundSource.Bundled("files/beep_weak.wav"),
        ),
    ),
    CLAVES(
        nameResourceId = "sound_library_claves",
        storageKey = "claves",
        sounds = GenericClickSounds(
            strongBeat = ClickSoundSource.Bundled("files/claves_strong.wav"),
            weakBeat = ClickSoundSource.Bundled("files/claves_weak.wav"),
        ),
    ),
    COWBELL(
        nameResourceId = "sound_library_cowbell",
        storageKey = "cowbell",
        sounds = GenericClickSounds(
            strongBeat = ClickSoundSource.Bundled("files/cowbell_strong.wav"),
            weakBeat = ClickSoundSource.Bundled("files/cowbell_weak.wav"),
        ),
    ),
    DRUMS(
        nameResourceId = "sound_library_drum_kit",
        storageKey = "drum kit",
        sounds = GenericClickSounds(
            strongBeat = ClickSoundSource.Bundled("files/drumkit_base.wav"),
            weakBeat = ClickSoundSource.Bundled("files/drumkit_hat.wav"),
        ),
    ),
}
