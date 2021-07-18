package com.vsevolodganin.clicktrack.state.redux.epic

import com.vsevolodganin.clicktrack.di.component.ViewModelScoped
import com.vsevolodganin.clicktrack.lib.ClickTrack
import com.vsevolodganin.clicktrack.model.ClickTrackWithDatabaseId
import com.vsevolodganin.clicktrack.model.DefaultCue
import com.vsevolodganin.clicktrack.state.logic.ClickTrackValidator
import com.vsevolodganin.clicktrack.state.logic.NewClickTrackNameSuggester
import com.vsevolodganin.clicktrack.state.redux.AppState
import com.vsevolodganin.clicktrack.state.redux.Screen
import com.vsevolodganin.clicktrack.state.redux.action.ClickTrackAction
import com.vsevolodganin.clicktrack.state.redux.action.EditClickTrackAction
import com.vsevolodganin.clicktrack.state.redux.action.NavigationAction
import com.vsevolodganin.clicktrack.state.redux.core.Action
import com.vsevolodganin.clicktrack.state.redux.core.Epic
import com.vsevolodganin.clicktrack.state.redux.core.Store
import com.vsevolodganin.clicktrack.state.redux.frontScreen
import com.vsevolodganin.clicktrack.storage.ClickTrackRepository
import com.vsevolodganin.clicktrack.utils.flow.consumeEach
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.transform

@ViewModelScoped
class ClickTrackEpic @Inject constructor(
    private val store: Store<AppState>,
    private val clickTrackRepository: ClickTrackRepository,
    private val newClickTrackNameSuggester: NewClickTrackNameSuggester,
    private val clickTrackValidator: ClickTrackValidator,
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return merge(
            store.state
                .map { it.backstack.screens.frontScreen() }
                .filterIsInstance<Screen.EditClickTrack>()
                .map { it.state }
                .distinctUntilChanged()
                .transform { editClickTrackState ->
                    val validationResult = clickTrackValidator.validate(editClickTrackState)
                    
                    clickTrackRepository.update(editClickTrackState.id, validationResult.validClickTrack)

                    emit(EditClickTrackAction.SetErrors(validationResult.errors))

                    validationResult.cueValidationResults.forEachIndexed { index, cueValidationResult ->
                        emit(EditClickTrackAction.EditCueAction.SetErrors(index, cueValidationResult.errors))
                    }
                },

            actions
                .filterIsInstance<ClickTrackAction.AddNew>()
                .map {
                    val suggestedNewClickTrackName = newClickTrackNameSuggester.suggest()
                    val newClickTrack = defaultNewClickTrack(suggestedNewClickTrackName)
                    val newClickTrackId = clickTrackRepository.insert(newClickTrack)
                    NavigationAction.ToEditClickTrackScreen(ClickTrackWithDatabaseId(newClickTrackId, newClickTrack))
                },

            actions
                .filterIsInstance<ClickTrackAction.Remove>()
                .consumeEach { action ->
                    clickTrackRepository.remove(action.id)
                },
        )
    }

    private fun defaultNewClickTrack(suggestedNewClickTrackName: String) = ClickTrack(
        name = suggestedNewClickTrackName,
        cues = listOf(DefaultCue),
        loop = true,
    )
}
