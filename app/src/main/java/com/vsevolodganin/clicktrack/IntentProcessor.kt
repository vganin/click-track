package com.vsevolodganin.clicktrack

import android.content.Context
import android.content.Intent
import com.vsevolodganin.clicktrack.di.component.ActivityScoped
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import com.vsevolodganin.clicktrack.state.redux.AppState
import com.vsevolodganin.clicktrack.state.redux.action.NavigationAction
import com.vsevolodganin.clicktrack.state.redux.core.Store
import javax.inject.Inject

@ActivityScoped
class IntentProcessor @Inject constructor(
    private val store: Store<AppState>,
) {
    fun process(intent: Intent) {
        when (intent.action) {
            Action.OPEN_CLICK_TRACK -> {
                val navigateAction = when (val clickTrackId = requireNotNull(intent.getParcelableExtra<ClickTrackId>(Extras.CLICK_TRACK_ID))) {
                    is ClickTrackId.Database -> NavigationAction.ToClickTrackScreen(clickTrackId)
                    ClickTrackId.Builtin.Metronome -> NavigationAction.ToMetronomeScreen
                    is ClickTrackId.Builtin.ClickSoundsTest -> return
                }
                store.dispatch(navigateAction)
            }
        }
    }
}

fun intentForLaunchAppWithClickTrack(context: Context, clickTrack: ClickTrackWithId): Intent {
    return Intent(context, MainActivity::class.java).apply {
        action = Action.OPEN_CLICK_TRACK
        putExtra(Extras.CLICK_TRACK_ID, clickTrack.id)
    }
}

private object Action {
    const val OPEN_CLICK_TRACK = "open_click_track"
}

private object Extras {
    const val CLICK_TRACK_ID = "click_track_id"
}
