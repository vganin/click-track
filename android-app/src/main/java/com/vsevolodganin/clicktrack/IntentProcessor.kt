package com.vsevolodganin.clicktrack

import android.app.Application
import android.content.Intent
import com.vsevolodganin.clicktrack.di.component.MainControllerScope
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.utils.decompose.resetTo
import me.tatarka.inject.annotations.Inject

@MainControllerScope
@Inject
class IntentProcessor(
    private val navigation: Navigation
) {
    fun process(intent: Intent) {
        when (intent.action) {
            Intent.ACTION_VIEW -> {
                when (intent.getStringExtra(Extras.DESTINATION)) {
                    Extras.DESTINATION_CLICK_TRACK -> {
                        val clickTrackId = intent.getLongExtra(Extras.CLICK_TRACK_ID, -1L)
                            .takeIf { it >= 0 }
                            ?.let(ClickTrackId::Database)
                            ?: throw IllegalArgumentException("No ${Extras.CLICK_TRACK_ID} supplied")
                        navigation.resetTo(ScreenConfiguration.PlayClickTrack(clickTrackId))
                    }
                    Extras.DESTINATION_POLYRHYTHMS -> navigation.resetTo(ScreenConfiguration.Polyrhythms)
                    Extras.DESTINATION_METRONOME -> navigation.resetTo(ScreenConfiguration.Metronome)
                }
            }
        }
    }
}

@Inject
class IntentFactory(private val application: Application) {

    fun navigate(id: ClickTrackId): Intent? {
        return when (id) {
            is ClickTrackId.Database -> navigateClickTrack(id)
            ClickTrackId.Builtin.Metronome -> navigateMetronome()
            is ClickTrackId.Builtin.ClickSoundsTest -> null
        }
    }

    fun navigateClickTrack(id: ClickTrackId.Database): Intent {
        return Intent(application, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            putExtra(Extras.DESTINATION, Extras.DESTINATION_CLICK_TRACK)
            putExtra(Extras.CLICK_TRACK_ID, id.value)
        }
    }

    fun navigatePolyrhythms(): Intent {
        return Intent(application, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            putExtra(Extras.DESTINATION, Extras.DESTINATION_POLYRHYTHMS)
        }
    }

    fun navigateMetronome(): Intent {
        return Intent(application, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            putExtra(Extras.DESTINATION, Extras.DESTINATION_METRONOME)
        }
    }
}

private object Extras {
    const val DESTINATION = "destination"
    const val DESTINATION_CLICK_TRACK = "click_track"
    const val DESTINATION_POLYRHYTHMS = "polyrhythms"
    const val DESTINATION_METRONOME = "metronome"
    const val CLICK_TRACK_ID = "click_track_id"
}
