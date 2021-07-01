package com.vsevolodganin.clicktrack.state.redux.action

import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.ClickTrackWithDatabaseId
import com.vsevolodganin.clicktrack.state.redux.core.Action

sealed interface ClickTrackAction : Action {

    class UpdateClickTrack(val clickTrack: ClickTrackWithDatabaseId) : ClickTrackAction

    class UpdateErrorInName(val id: ClickTrackId.Database, val isPresent: Boolean) : ClickTrackAction

    object AddNewClickTrack : ClickTrackAction

    class RemoveClickTrack(val id: ClickTrackId.Database) : ClickTrackAction
}
