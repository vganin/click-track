package com.vsevolodganin.clicktrack.state.actions

import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import com.vsevolodganin.clicktrack.redux.Action

sealed interface ClickTrackListAction : Action {

    object SubscribeToData : ClickTrackListAction {
        object Dispose : ClickTrackListAction
    }

    class SetData(val data: List<ClickTrackWithId>) : ClickTrackListAction
}
