package com.vsevolodganin.clicktrack.state.epic

import com.vsevolodganin.clicktrack.di.component.ViewModelScoped
import com.vsevolodganin.clicktrack.lib.BuiltinClickSounds
import com.vsevolodganin.clicktrack.lib.ClickTrack
import com.vsevolodganin.clicktrack.lib.Cue
import com.vsevolodganin.clicktrack.lib.CueDuration
import com.vsevolodganin.clicktrack.lib.CueWithDuration
import com.vsevolodganin.clicktrack.lib.TimeSignature
import com.vsevolodganin.clicktrack.lib.bpm
import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.redux.Epic
import com.vsevolodganin.clicktrack.state.actions.NavigateToEditClickTrackScreen
import com.vsevolodganin.clicktrack.state.actions.StoreAddNewClickTrack
import com.vsevolodganin.clicktrack.state.actions.StoreUpdateClickTrack
import com.vsevolodganin.clicktrack.state.utils.NewClickTrackNameSuggester
import com.vsevolodganin.clicktrack.storage.ClickTrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

@ViewModelScoped
class SaveClickTrackEpic @Inject constructor(
    private val storage: ClickTrackRepository,
    private val newClickTrackNameSuggester: NewClickTrackNameSuggester,
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return merge(
            actions
                .filterIsInstance<StoreAddNewClickTrack>()
                .map {
                    val suggestedNewClickTrackName = newClickTrackNameSuggester.suggest()
                    val newClickTrack = storage.insert(defaultNewClickTrack(suggestedNewClickTrackName))
                    NavigateToEditClickTrackScreen(newClickTrack)
                },
            actions.filterIsInstance<StoreUpdateClickTrack>()
                .transform {
                    val clickTrack = it.clickTrack
                    val (validatedClickTrack, isErrorInName) = validate(clickTrack.value)
                    emit(StoreUpdateClickTrack.Result(clickTrack, isErrorInName))
                    if (!isErrorInName) {
                        storage.update(clickTrack.copy(value = validatedClickTrack))
                    }
                },
        )
    }

    private fun defaultNewClickTrack(suggestedNewClickTrackName: String) = ClickTrack(
        name = suggestedNewClickTrackName,
        cues = listOf(
            CueWithDuration(
                duration = CueDuration.Measures(1),
                cue = Cue(60.bpm, TimeSignature(4, 4))
            )
        ),
        loop = true,
        sounds = BuiltinClickSounds,
    )

    private fun validate(clickTrack: ClickTrack): ClickTrackValidationResult {
        val name = clickTrack.name.trim()
        val isErrorInName = name.isEmpty()
        return ClickTrackValidationResult(
            validatedClickTrack = clickTrack.copy(name = name),
            isErrorInName = isErrorInName,
        )
    }

    private data class ClickTrackValidationResult(
        val validatedClickTrack: ClickTrack,
        val isErrorInName: Boolean,
    )
}
