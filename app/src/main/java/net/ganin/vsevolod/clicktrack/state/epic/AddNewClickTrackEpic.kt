package net.ganin.vsevolod.clicktrack.state.epic

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import net.ganin.vsevolod.clicktrack.lib.ClickTrack
import net.ganin.vsevolod.clicktrack.lib.ClickTrackWithMeta
import net.ganin.vsevolod.clicktrack.lib.Cue
import net.ganin.vsevolod.clicktrack.lib.CueDuration
import net.ganin.vsevolod.clicktrack.lib.CueWithDuration
import net.ganin.vsevolod.clicktrack.lib.TimeSignature
import net.ganin.vsevolod.clicktrack.lib.bpm
import net.ganin.vsevolod.clicktrack.redux.Action
import net.ganin.vsevolod.clicktrack.redux.Epic
import net.ganin.vsevolod.clicktrack.state.actions.AddNewClickTrack
import net.ganin.vsevolod.clicktrack.state.actions.NavigateToEditClickTrackScreen
import net.ganin.vsevolod.clicktrack.state.utils.NewClickTrackNameSuggester

class AddNewClickTrackEpic(private val newClickTrackNameSuggester: NewClickTrackNameSuggester) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return actions
            .filterIsInstance<AddNewClickTrack>()
            .map {
                val suggestedNewClickTrackName = newClickTrackNameSuggester.suggestNewClickTrackName()
                val newClickTrack = defaultNewClickTrack(suggestedNewClickTrackName)
                NavigateToEditClickTrackScreen(newClickTrack)
            }
    }

    private fun defaultNewClickTrack(suggestedNewClickTrackName: String) = ClickTrackWithMeta(
        name = suggestedNewClickTrackName,
        clickTrack = ClickTrack(
            cues = listOf(
                CueWithDuration(CueDuration.Beats(4), Cue(60.bpm, TimeSignature(4, 4)))
            ),
            loop = true
        )
    )
}
