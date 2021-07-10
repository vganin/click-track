package com.vsevolodganin.clicktrack.state.redux.epic

import com.vsevolodganin.clicktrack.di.component.ViewModelScoped
import com.vsevolodganin.clicktrack.lib.applyDiff
import com.vsevolodganin.clicktrack.meter.BpmMeter
import com.vsevolodganin.clicktrack.state.logic.ClickTrackValidator
import com.vsevolodganin.clicktrack.state.redux.action.MetronomeAction
import com.vsevolodganin.clicktrack.state.redux.core.Action
import com.vsevolodganin.clicktrack.state.redux.core.Epic
import com.vsevolodganin.clicktrack.storage.UserPreferencesRepository
import com.vsevolodganin.clicktrack.utils.flow.consumeEach
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge

@ViewModelScoped
class MetronomeEpic @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val bpmMeter: BpmMeter,
    private val clickTrackValidator: ClickTrackValidator,
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return merge(
            actions.filterIsInstance<MetronomeAction.SetBpm>()
                .consumeEach { action ->
                    userPreferencesRepository.metronomeBpm.edit {
                        clickTrackValidator.limitBpm(action.bpm)
                    }
                },

            actions.filterIsInstance<MetronomeAction.ChangeBpm>()
                .consumeEach { action ->
                    userPreferencesRepository.metronomeBpm.edit { bpm ->
                        clickTrackValidator.limitBpm(bpm.applyDiff(action.by))
                    }
                },

            actions.filterIsInstance<MetronomeAction.SetPattern>()
                .consumeEach { action ->
                    userPreferencesRepository.metronomePattern.edit {
                        action.pattern
                    }
                },

            actions.filterIsInstance<MetronomeAction.BpmMeterTap>()
                .mapNotNull {
                    bpmMeter.addTap()
                    bpmMeter.calculateBpm()?.let(MetronomeAction::SetBpm)
                },
        )
    }
}
