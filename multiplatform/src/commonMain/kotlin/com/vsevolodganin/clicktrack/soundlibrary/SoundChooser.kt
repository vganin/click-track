package com.vsevolodganin.clicktrack.soundlibrary

import com.vsevolodganin.clicktrack.model.ClickSoundType
import com.vsevolodganin.clicktrack.model.ClickSoundsId

expect class SoundChooser {
    suspend fun launchFor(id: ClickSoundsId.Database, type: ClickSoundType)
}
