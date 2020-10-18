package net.ganin.vsevolod.clicktrack.state.epic

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import net.ganin.vsevolod.clicktrack.di.component.ViewModelScoped
import net.ganin.vsevolod.clicktrack.lib.ClickTrack
import net.ganin.vsevolod.clicktrack.lib.Cue
import net.ganin.vsevolod.clicktrack.lib.CueDuration
import net.ganin.vsevolod.clicktrack.lib.CueWithDuration
import net.ganin.vsevolod.clicktrack.lib.TimeSignature
import net.ganin.vsevolod.clicktrack.lib.bpm
import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.redux.Epic
import net.ganin.vsevolod.clicktrack.state.actions.AddNewClickTrack
import net.ganin.vsevolod.clicktrack.state.actions.NavigateToEditClickTrackScreen
import net.ganin.vsevolod.clicktrack.state.actions.UpdateClickTrack
import net.ganin.vsevolod.clicktrack.state.utils.NewClickTrackNameSuggester
import net.ganin.vsevolod.clicktrack.storage.ClickTrackRepository
import net.ganin.vsevolod.clicktrack.utils.flow.consumeEach
import javax.inject.Inject

@ViewModelScoped
class SaveClickTrackEpic @Inject constructor(
    private val storage: ClickTrackRepository,
    private val newClickTrackNameSuggester: NewClickTrackNameSuggester
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return merge(
            actions
                .filterIsInstance<AddNewClickTrack>()
                .map {
                    val suggestedNewClickTrackName = newClickTrackNameSuggester.suggest()
                    val newClickTrack = storage.insert(defaultNewClickTrack(suggestedNewClickTrackName))
                    NavigateToEditClickTrackScreen(newClickTrack)
                },
            actions.filterIsInstance<UpdateClickTrack>()
                .consumeEach {
                    storage.update(it.clickTrack)
                },
        )
    }

    private fun defaultNewClickTrack(suggestedNewClickTrackName: String) = ClickTrack(
        name = suggestedNewClickTrackName,
        cues = listOf(
            CueWithDuration(CueDuration.Beats(4), Cue(60.bpm, TimeSignature(4, 4)))
        ),
        loop = true
    )
}
