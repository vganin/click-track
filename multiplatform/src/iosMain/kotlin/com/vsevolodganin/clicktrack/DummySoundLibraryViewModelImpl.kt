package com.vsevolodganin.clicktrack

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.pop
import com.vsevolodganin.clicktrack.model.BuiltinClickSounds
import com.vsevolodganin.clicktrack.model.ClickSoundType
import com.vsevolodganin.clicktrack.model.ClickSoundsId
import com.vsevolodganin.clicktrack.soundlibrary.SelectableClickSoundsItem
import com.vsevolodganin.clicktrack.soundlibrary.SoundLibraryState
import com.vsevolodganin.clicktrack.soundlibrary.SoundLibraryViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class DummySoundLibraryViewModelImpl(
    @Assisted componentContext: ComponentContext,
    private val navigation: Navigation,
) : SoundLibraryViewModel, ComponentContext by componentContext {
    override val state: StateFlow<SoundLibraryState?> = MutableStateFlow(
        SoundLibraryState(
            items = listOf(
                SelectableClickSoundsItem.Builtin(
                    data = BuiltinClickSounds.BEEP,
                    selected = true
                ),
                SelectableClickSoundsItem.UserDefined(
                    id = ClickSoundsId.Database(0L),
                    strongBeatValue = "/audio/audio/audio/audio/strong.mp3",
                    weakBeatValue = "/audio/audio/audio/audio/weak.mp3",
                    hasError = false,
                    isPlaying = true,
                    selected = false
                ),
                SelectableClickSoundsItem.UserDefined(
                    id = ClickSoundsId.Database(1L),
                    strongBeatValue = "/audio/audio/audio/audio/strong.mp3",
                    weakBeatValue = "no_access.mp3",
                    hasError = true,
                    isPlaying = false,
                    selected = false
                )
            ),
        )
    )

    override fun onBackClick() = navigation.pop()
    override fun onAddNewClick() = Unit
    override fun onItemClick(id: ClickSoundsId) = Unit
    override fun onItemRemove(id: ClickSoundsId.Database) = Unit
    override fun onItemSoundSelect(id: ClickSoundsId.Database, type: ClickSoundType) = Unit
    override fun onItemSoundTestToggle(id: ClickSoundsId.Database) = Unit
}
