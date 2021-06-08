package com.vsevolodganin.clicktrack.state.actions

import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.state.PlaybackState

sealed interface ClickTrackAction : Action {

    class SubscribeToData(val id: ClickTrackId.Database) : ClickTrackAction {
        object Dispose : ClickTrackAction
    }

    class UpdateClickTrack(val data: ClickTrackWithId, val shouldStore: Boolean) : ClickTrackAction

    class UpdateErrorInName(val id: ClickTrackId.Database, val isPresent: Boolean) : ClickTrackAction

    object NewClickTrack : ClickTrackAction

    class RemoveClickTrack(val id: ClickTrackId.Database, val shouldStore: Boolean) : ClickTrackAction

    class UpdateCurrentlyPlaying(val playbackState: PlaybackState?) : ClickTrackAction

    class StartPlay(val clickTrack: ClickTrackWithId, val progress: Double? = null) : ClickTrackAction

    object StopPlay : ClickTrackAction

    object PausePlay : ClickTrackAction
}
