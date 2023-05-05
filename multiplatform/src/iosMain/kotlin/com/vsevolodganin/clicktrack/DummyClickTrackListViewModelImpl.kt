package com.vsevolodganin.clicktrack

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.push
import com.vsevolodganin.clicktrack.list.ClickTrackListState
import com.vsevolodganin.clicktrack.list.ClickTrackListViewModel
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.ui.preview.PREVIEW_CLICK_TRACK_1
import com.vsevolodganin.clicktrack.ui.preview.PREVIEW_CLICK_TRACK_2
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class DummyClickTrackListViewModelImpl(
    @Assisted componentContext: ComponentContext,
    private val navigation: Navigation,
) : ClickTrackListViewModel, ComponentContext by componentContext {

    override val state: StateFlow<ClickTrackListState> = MutableStateFlow(
        ClickTrackListState(
            listOf(
                PREVIEW_CLICK_TRACK_1,
                PREVIEW_CLICK_TRACK_2,
            )
        )
    )

    override fun onAddClick() = Unit
    override fun onItemClick(id: ClickTrackId.Database) = navigation.push(ScreenConfiguration.PlayClickTrack(id))
    override fun onItemRemove(id: ClickTrackId.Database) = Unit
    override fun onMenuClick() = navigation.openDrawer()
}
