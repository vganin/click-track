package com.vsevolodganin.clicktrack.redux.epic

import com.vsevolodganin.clicktrack.di.component.ActivityScoped
import com.vsevolodganin.clicktrack.redux.action.BackstackAction
import com.vsevolodganin.clicktrack.redux.action.ComputingNavigationAction
import com.vsevolodganin.clicktrack.redux.core.Action
import com.vsevolodganin.clicktrack.redux.core.Epic
import com.vsevolodganin.clicktrack.redux.toCommon
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

@ActivityScoped
class NavigationEpic @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return merge(
            actions.filterIsInstance<ComputingNavigationAction.ToTrainingScreen>()
                .map {
                    val persistableState = userPreferencesRepository.trainingState.stateFlow.first()
                    BackstackAction.ToTrainingScreen(
                        state = persistableState.toCommon()
                    )
                }
        )
    }
}
