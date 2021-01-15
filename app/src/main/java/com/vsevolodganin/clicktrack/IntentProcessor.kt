package com.vsevolodganin.clicktrack

import android.content.Context
import android.content.Intent
import com.vsevolodganin.clicktrack.di.component.ActivityScoped
import com.vsevolodganin.clicktrack.model.ClickTrackWithId
import com.vsevolodganin.clicktrack.model.MetronomeId
import com.vsevolodganin.clicktrack.redux.Store
import com.vsevolodganin.clicktrack.state.AppState
import com.vsevolodganin.clicktrack.state.actions.NavigateToClickTrackScreen
import com.vsevolodganin.clicktrack.state.actions.NavigateToMetronomeScreen
import javax.inject.Inject

@ActivityScoped
class IntentProcessor @Inject constructor(
    private val store: Store<AppState>,
) {
    fun process(intent: Intent) {
        when (intent.action) {
            Action.OPEN_CLICK_TRACK -> {
                val clickTrack = requireNotNull(intent.getParcelableExtra<ClickTrackWithId>(Extras.CLICK_TRACK))
                val navigateAction = if (clickTrack.id == MetronomeId) {
                    NavigateToMetronomeScreen
                } else {
                    NavigateToClickTrackScreen(clickTrack)
                }
                store.dispatch(navigateAction)
            }
        }
    }
}

fun intentForLaunchAppWithClickTrack(context: Context, clickTrack: ClickTrackWithId): Intent {
    return Intent(context, MainActivity::class.java).apply {
        action = Action.OPEN_CLICK_TRACK
        putExtra(Extras.CLICK_TRACK, clickTrack)
    }
}

private object Action {
    const val OPEN_CLICK_TRACK = "open_click_track"
}

private object Extras {
    const val CLICK_TRACK = "click_track"
}
