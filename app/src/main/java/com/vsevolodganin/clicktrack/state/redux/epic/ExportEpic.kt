package com.vsevolodganin.clicktrack.state.redux.epic

import com.vsevolodganin.clicktrack.di.component.ActivityScoped
import com.vsevolodganin.clicktrack.state.logic.ClickTrackExporter
import com.vsevolodganin.clicktrack.state.redux.action.ExportAction
import com.vsevolodganin.clicktrack.state.redux.core.Action
import com.vsevolodganin.clicktrack.state.redux.core.Epic
import com.vsevolodganin.clicktrack.utils.flow.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

@ActivityScoped
class ExportEpic @Inject constructor(
    private val exporter: ClickTrackExporter
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return merge(
            actions.filterIsInstance<ExportAction.Start>()
                .consumeEach {
                    exporter.start(it.clickTrack)
                },
            actions.filterIsInstance<ExportAction.Stop>()
                .consumeEach {
                    exporter.cancel()
                },
        )
    }
}
