package com.vsevolodganin.clicktrack.state.epic

import com.vsevolodganin.clicktrack.di.component.ViewModelScoped
import com.vsevolodganin.clicktrack.lib.ClickTrack
import com.vsevolodganin.clicktrack.lib.Cue
import com.vsevolodganin.clicktrack.lib.CueDuration
import com.vsevolodganin.clicktrack.lib.TimeSignature
import com.vsevolodganin.clicktrack.lib.bpm
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.redux.Epic
import com.vsevolodganin.clicktrack.redux.Store
import com.vsevolodganin.clicktrack.state.AppState
import com.vsevolodganin.clicktrack.state.Screen
import com.vsevolodganin.clicktrack.state.actions.ClickTrackAction
import com.vsevolodganin.clicktrack.state.actions.NavigationAction
import com.vsevolodganin.clicktrack.state.utils.NewClickTrackNameSuggester
import com.vsevolodganin.clicktrack.state.utils.onScreen
import com.vsevolodganin.clicktrack.storage.ClickTrackRepository
import com.vsevolodganin.clicktrack.utils.flow.consumeEach
import com.vsevolodganin.clicktrack.utils.optionalCast
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.transform

@ViewModelScoped
class ClickTrackEpic @Inject constructor(
    private val store: Store<AppState>,
    private val clickTrackRepository: ClickTrackRepository,
    private val newClickTrackNameSuggester: NewClickTrackNameSuggester,
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return merge(
            store.onScreen<Screen.ClickTrackList> {
                clickTrackRepository.getAll().map(ClickTrackAction::UpdateClickTrackList)
            },

            store.onScreen<Screen.PlayClickTrack> { screen ->
                val databaseId = screen.state.clickTrack.id.optionalCast<ClickTrackId.Database>() ?: return@onScreen emptyFlow()
                clickTrackRepository.getById(databaseId)
                    .filterNotNull()
                    .map { track -> ClickTrackAction.UpdateClickTrack(data = track, shouldStore = false) }
            },

            actions
                .filterIsInstance<ClickTrackAction.NewClickTrack>()
                .map {
                    val suggestedNewClickTrackName = newClickTrackNameSuggester.suggest()
                    val newClickTrack = clickTrackRepository.insert(defaultNewClickTrack(suggestedNewClickTrackName))
                    NavigationAction.ToEditClickTrackScreen(newClickTrack)
                },

            actions
                .filterIsInstance<ClickTrackAction.RemoveClickTrack>()
                .consumeEach { action ->
                    if (action.shouldStore) {
                        clickTrackRepository.remove(action.id)
                    }
                },

            actions.filterIsInstance<ClickTrackAction.UpdateClickTrack>()
                .transform { action ->
                    if (action.shouldStore) {
                        val clickTrack = action.data
                        val databaseId = clickTrack.id.optionalCast<ClickTrackId.Database>() ?: return@transform
                        val (validatedClickTrack, isErrorInName) = validate(clickTrack.value)
                        emit(ClickTrackAction.UpdateErrorInName(databaseId, isErrorInName))
                        if (!isErrorInName) {
                            clickTrackRepository.update(databaseId, validatedClickTrack)
                        }
                    }
                },
        )
    }

    private fun defaultNewClickTrack(suggestedNewClickTrackName: String) = ClickTrack(
        name = suggestedNewClickTrackName,
        cues = listOf(
            Cue(
                bpm = 60.bpm,
                timeSignature = TimeSignature(4, 4),
                duration = CueDuration.Measures(1),
            )
        ),
        loop = true,
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
