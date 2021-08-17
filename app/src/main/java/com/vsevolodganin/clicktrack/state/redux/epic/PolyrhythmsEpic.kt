package com.vsevolodganin.clicktrack.state.redux.epic

import com.vsevolodganin.clicktrack.state.redux.action.PolyrhythmsAction
import com.vsevolodganin.clicktrack.state.redux.core.Action
import com.vsevolodganin.clicktrack.state.redux.core.Epic
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.utils.flow.consumeEach
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.merge

class PolyrhythmsEpic @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return merge(
            actions.filterIsInstance<PolyrhythmsAction.EditLayer1>()
                .consumeEach { action ->
                    userPreferencesRepository.polyrhythm.edit {
                        it.copy(
                            layer1 = action.number.coerceIn(BEATS_NUMBER_RANGE)
                        )
                    }
                },

            actions.filterIsInstance<PolyrhythmsAction.EditLayer2>()
                .consumeEach { action ->
                    userPreferencesRepository.polyrhythm.edit {
                        it.copy(
                            layer2 = action.number.coerceIn(BEATS_NUMBER_RANGE)
                        )
                    }
                },
        )
    }
}

private val BEATS_NUMBER_RANGE = 1..32
