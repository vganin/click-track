package com.vsevolodganin.clicktrack.state.redux.action

import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.state.redux.core.Action

sealed interface ClickTrackAction : Action {

    object AddNew : ClickTrackAction

    class Remove(val id: ClickTrackId.Database) : ClickTrackAction
}
