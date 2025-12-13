package com.vsevolodganin.clicktrack

import com.vsevolodganin.clicktrack.di.component.MainControllerScope
import com.vsevolodganin.clicktrack.model.ClickSoundType
import com.vsevolodganin.clicktrack.model.ClickSoundsId
import com.vsevolodganin.clicktrack.soundlibrary.SoundChooser
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(MainControllerScope::class)
@Inject
class DummySoundChooserImpl : SoundChooser {
    override suspend fun launchFor(id: ClickSoundsId.Database, type: ClickSoundType) = Unit
}
