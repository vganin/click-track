package com.vsevolodganin.clicktrack.soundlibrary

import com.vsevolodganin.clicktrack.model.ClickSoundType
import com.vsevolodganin.clicktrack.model.ClickSoundsId
import kotlinx.coroutines.flow.StateFlow

interface SoundLibraryViewModel {
    val state: StateFlow<SoundLibraryState?>

    fun onBackClick()

    fun onAddNewClick()

    fun onItemClick(id: ClickSoundsId)

    fun onItemRemove(id: ClickSoundsId.Database)

    fun onItemSoundSelect(id: ClickSoundsId.Database, type: ClickSoundType)

    fun onItemSoundTestToggle(id: ClickSoundsId.Database)
}
