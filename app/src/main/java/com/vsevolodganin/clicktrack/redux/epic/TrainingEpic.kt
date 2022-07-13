package com.vsevolodganin.clicktrack.redux.epic

import com.vsevolodganin.clicktrack.common.NewClickTrackNameSuggester
import com.vsevolodganin.clicktrack.common.TrainingClickTrackGenerator
import com.vsevolodganin.clicktrack.common.TrainingStateValidator
import com.vsevolodganin.clicktrack.di.component.ViewModelScoped
import com.vsevolodganin.clicktrack.redux.AppState
import com.vsevolodganin.clicktrack.redux.Screen
import com.vsevolodganin.clicktrack.redux.action.BackstackAction
import com.vsevolodganin.clicktrack.redux.action.TrainingAction
import com.vsevolodganin.clicktrack.redux.core.Action
import com.vsevolodganin.clicktrack.redux.core.Epic
import com.vsevolodganin.clicktrack.redux.core.Store
import com.vsevolodganin.clicktrack.redux.epic.TrainingEpic.Const.NEW_TRAINING_CLICK_TRACK_NAME
import com.vsevolodganin.clicktrack.storage.ClickTrackRepository
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

@ViewModelScoped
class TrainingEpic @Inject constructor(
    private val store: Store<AppState>,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val clickTrackRepository: ClickTrackRepository,
    private val stateValidator: TrainingStateValidator,
    private val newClickTrackNameSuggester: NewClickTrackNameSuggester,
    private val trainingClickTrackGenerator: TrainingClickTrackGenerator,
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return merge(
            store.state
                .map { it.backstack.frontScreen }
                .filterIsInstance<Screen.Training>()
                .map { it.state }
                .distinctUntilChanged()
                .map { state ->
                    val stateValidationResult = stateValidator.validate(state)

                    val persistableState = stateValidationResult.persistableState
                    if (persistableState != null) {
                        userPreferencesRepository.trainingState.edit { persistableState }
                    }

                    TrainingAction.UpdateErrors(errors = stateValidationResult.errors)
                },

            actions.filterIsInstance<TrainingAction.Accept>()
                .map {
                    val trainingState = userPreferencesRepository.trainingState.stateFlow.first()
                    val suggestedName = newClickTrackNameSuggester.suggest(NEW_TRAINING_CLICK_TRACK_NAME)
                    val newClickTrack = trainingClickTrackGenerator.generate(trainingState, suggestedName)
                    val newClickTrackId = clickTrackRepository.insert(newClickTrack)
                    BackstackAction.ToClickTrackScreen(newClickTrackId)
                }
        )
    }

    private object Const {
        const val NEW_TRAINING_CLICK_TRACK_NAME = "Training"
    }
}
