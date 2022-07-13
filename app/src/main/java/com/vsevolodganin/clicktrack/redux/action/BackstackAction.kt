package com.vsevolodganin.clicktrack.redux.action

import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.ClickTrackWithDatabaseId
import com.vsevolodganin.clicktrack.redux.TrainingState
import com.vsevolodganin.clicktrack.redux.core.Action

sealed interface BackstackAction : Action {

    object Pop : BackstackAction

    object ToClickTrackListScreen : BackstackAction

    class ToClickTrackScreen(val id: ClickTrackId.Database) : BackstackAction

    class ToEditClickTrackScreen(val clickTrack: ClickTrackWithDatabaseId, val isInitialEdit: Boolean) : BackstackAction

    object ToMetronomeScreen : BackstackAction

    class ToTrainingScreen(val state: TrainingState) : BackstackAction

    object ToSettingsScreen : BackstackAction

    object ToSoundLibraryScreen : BackstackAction

    object ToAboutScreen : BackstackAction

    object ToPolyrhythms : BackstackAction
}

sealed interface ComputingNavigationAction : Action {

    object ToTrainingScreen : ComputingNavigationAction
}
