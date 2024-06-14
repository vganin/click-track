package com.vsevolodganin.clicktrack.list

import com.vsevolodganin.clicktrack.model.ClickTrackId
import kotlinx.coroutines.flow.StateFlow

interface ClickTrackListViewModel {
    val state: StateFlow<ClickTrackListState>

    fun onAddClick()

    fun onItemClick(id: ClickTrackId.Database)

    fun onItemRemove(id: ClickTrackId.Database)

    fun onMenuClick()

    fun onItemMove(from: Int, to: Int)

    fun onItemMoveFinished()
}
