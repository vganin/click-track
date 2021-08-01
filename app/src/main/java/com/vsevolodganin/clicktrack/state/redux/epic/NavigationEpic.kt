package com.vsevolodganin.clicktrack.state.redux.epic

import com.vsevolodganin.clicktrack.di.component.ActivityScoped
import com.vsevolodganin.clicktrack.state.redux.action.ComputingNavigationAction
import com.vsevolodganin.clicktrack.state.redux.action.NavigationAction
import com.vsevolodganin.clicktrack.state.redux.core.Action
import com.vsevolodganin.clicktrack.state.redux.core.Epic
import com.vsevolodganin.clicktrack.state.redux.toCommon
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

@ActivityScoped
class NavigationEpic @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return merge(
            actions.filterIsInstance<ComputingNavigationAction.ToTrainingScreen>()
                .map {
                    val persistableState = userPreferencesRepository.trainingState.flow.first()
                    NavigationAction.ToTrainingScreen(
                        state = persistableState.toCommon()
                    )
                }
        )
    }
}
