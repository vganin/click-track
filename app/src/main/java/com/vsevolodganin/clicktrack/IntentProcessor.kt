package com.vsevolodganin.clicktrack

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import com.vsevolodganin.clicktrack.di.component.ActivityScoped
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.state.redux.AppState
import com.vsevolodganin.clicktrack.state.redux.action.NavigationAction
import com.vsevolodganin.clicktrack.state.redux.core.Store
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@ActivityScoped
class IntentProcessor @Inject constructor(
    private val store: Store<AppState>,
) {
    fun process(intent: Intent) {
        when (intent.action) {
            Action.NAVIGATE -> {
                val navigateAction = when (intent.getParcelableExtra<NavigationDestination>(Extras.NAVIGATION_DESTINATION)) {
                    NavigationDestination.CLICK_TRACK -> when (val clickTrackId =
                        intent.getParcelableExtra<ClickTrackId>(Extras.CLICK_TRACK_ID)) {
                        is ClickTrackId.Database -> NavigationAction.ToClickTrackScreen(clickTrackId)
                        ClickTrackId.Builtin.Metronome -> NavigationAction.ToMetronomeScreen
                        is ClickTrackId.Builtin.ClickSoundsTest,
                        null -> return
                    }
                    NavigationDestination.POLYRHYTHMS -> NavigationAction.ToPolyrhythms
                    null -> return
                }
                store.dispatch(navigateAction)
            }
        }
    }
}

class IntentFactory @Inject constructor(private val context: Context) {

    fun openClickTrack(clickTrackId: ClickTrackId): Intent {
        return Intent(context, MainActivity::class.java).apply {
            action = Action.NAVIGATE
            putExtra(Extras.NAVIGATION_DESTINATION, NavigationDestination.CLICK_TRACK as Parcelable)
            putExtra(Extras.CLICK_TRACK_ID, clickTrackId)
        }
    }

    fun openPolyrhythms(): Intent {
        return Intent(context, MainActivity::class.java).apply {
            action = Action.NAVIGATE
            putExtra(Extras.NAVIGATION_DESTINATION, NavigationDestination.POLYRHYTHMS as Parcelable)
        }
    }
}

private object Action {
    const val NAVIGATE = "navigate"
}

private object Extras {
    const val NAVIGATION_DESTINATION = "navigation_destination"
    const val CLICK_TRACK_ID = "click_track_id"
}

@Parcelize
private enum class NavigationDestination : Parcelable {
    CLICK_TRACK, POLYRHYTHMS
}
