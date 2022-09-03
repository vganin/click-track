package com.vsevolodganin.clicktrack.player

import android.app.Application
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.di.component.PlayerServiceScope
import com.vsevolodganin.clicktrack.metronome.metronomeClickTrack
import com.vsevolodganin.clicktrack.model.ClickTrack
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.TwoLayerPolyrhythm
import com.vsevolodganin.clicktrack.soundlibrary.soundTestClickTrack
import com.vsevolodganin.clicktrack.storage.ClickTrackRepository
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@PlayerServiceScope
class PlayableContentProvider @Inject constructor(
    private val application: Application,
    private val clickTrackRepository: ClickTrackRepository,
    private val userPreferences: UserPreferencesRepository
) {
    fun clickTrackFlow(id: ClickTrackId): Flow<ClickTrack?> {
        return when (id) {
            is ClickTrackId.Database -> {
                clickTrackRepository.getById(id).map { it?.value }
            }
            is ClickTrackId.Builtin.ClickSoundsTest -> {
                flowOf(soundTestClickTrack())
            }
            is ClickTrackId.Builtin.Metronome -> {
                combine(
                    userPreferences.metronomeBpm.flow,
                    userPreferences.metronomePattern.flow,
                ) { bpm, pattern ->
                    metronomeClickTrack(
                        name = application.getString(R.string.general_metronome_click_track_title),
                        bpm = bpm,
                        pattern = pattern,
                    )
                }
            }
        }
    }

    fun twoLayerPolyrhythmFlow(): Flow<TwoLayerPolyrhythm> = userPreferences.polyrhythm.flow
}
