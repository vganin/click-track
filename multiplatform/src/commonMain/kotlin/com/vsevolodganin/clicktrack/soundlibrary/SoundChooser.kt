package com.vsevolodganin.clicktrack.soundlibrary

import com.vsevolodganin.clicktrack.model.ClickSoundType
import com.vsevolodganin.clicktrack.model.ClickSoundsId

interface SoundChooser {
    suspend fun launchFor(id: ClickSoundsId.Database, type: ClickSoundType)
}
