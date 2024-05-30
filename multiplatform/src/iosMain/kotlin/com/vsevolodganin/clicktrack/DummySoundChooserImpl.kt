package com.vsevolodganin.clicktrack

import com.vsevolodganin.clicktrack.di.component.MainControllerScope
import com.vsevolodganin.clicktrack.model.ClickSoundType
import com.vsevolodganin.clicktrack.model.ClickSoundsId
import com.vsevolodganin.clicktrack.soundlibrary.SoundChooser
import me.tatarka.inject.annotations.Inject

@MainControllerScope
@Inject
class DummySoundChooserImpl : SoundChooser {
    override suspend fun launchFor(
        id: ClickSoundsId.Database,
        type: ClickSoundType,
    ) = Unit
}
