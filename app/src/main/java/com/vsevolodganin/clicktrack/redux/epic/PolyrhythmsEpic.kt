package com.vsevolodganin.clicktrack.redux.epic

import com.vsevolodganin.clicktrack.redux.action.PolyrhythmsAction
import com.vsevolodganin.clicktrack.redux.core.Action
import com.vsevolodganin.clicktrack.redux.core.Epic
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.utils.flow.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

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
