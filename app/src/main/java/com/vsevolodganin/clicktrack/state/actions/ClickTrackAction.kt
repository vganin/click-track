@file:Suppress("FunctionName") // Constructor functions should be named as class

package com.vsevolodganin.clicktrack.state.actions

import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.state.PlaybackState

sealed class ClickTrackAction : Action {

    class UpdateClickTrackList(val data: List<ClickTrackWithId>) : ClickTrackAction()

    class UpdateClickTrack(val data: ClickTrackWithId, val shouldStore: Boolean) : ClickTrackAction()

    class UpdateErrorInName(val id: ClickTrackId.Database, val isPresent: Boolean) : ClickTrackAction()

    object NewClickTrack : ClickTrackAction()

    class RemoveClickTrack(val id: ClickTrackId.Database, val shouldStore: Boolean) : ClickTrackAction()

    class UpdateCurrentlyPlaying(val playbackState: PlaybackState?) : ClickTrackAction()

    class StartPlay(val clickTrack: ClickTrackWithId, val progress: Double? = null) : ClickTrackAction()

    object StopPlay : ClickTrackAction()

    object PausePlay : ClickTrackAction()
}
