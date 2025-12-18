package com.vsevolodganin.clicktrack.player

import clicktrack.multiplatform.generated.resources.general_metronome_click_track_title
import com.vsevolodganin.clicktrack.di.component.PlayerServiceScope
import com.vsevolodganin.clicktrack.metronome.metronomeClickTrack
import com.vsevolodganin.clicktrack.model.ClickTrack
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.TwoLayerPolyrhythm
import com.vsevolodganin.clicktrack.soundlibrary.soundTestClickTrack
import com.vsevolodganin.clicktrack.storage.ClickTrackRepository
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.utils.MultiplatformRes
import com.vsevolodganin.clicktrack.utils.string
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.jetbrains.compose.resources.getString

@SingleIn(PlayerServiceScope::class)
@Inject
class PlayableContentProvider(
    private val clickTrackRepository: ClickTrackRepository,
    private val userPreferences: UserPreferencesRepository,
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
                    userPreferences.metronomeTimeSignature.flow,
                ) { bpm, pattern, timeSignature ->
                    metronomeClickTrack(
                        name = getString(MultiplatformRes.string.general_metronome_click_track_title),
                        bpm = bpm,
                        pattern = pattern,
                        timeSignature = timeSignature,
                    )
                }
            }
        }
    }

    fun twoLayerPolyrhythmFlow(): Flow<TwoLayerPolyrhythm> = userPreferences.polyrhythm.flow
}
