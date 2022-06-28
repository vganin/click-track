package com.vsevolodganin.clicktrack.redux.epic

import com.vsevolodganin.clicktrack.di.component.ActivityScoped
import com.vsevolodganin.clicktrack.export.ExportWorkLauncher
import com.vsevolodganin.clicktrack.redux.action.ExportAction
import com.vsevolodganin.clicktrack.redux.core.Action
import com.vsevolodganin.clicktrack.redux.core.Epic
import com.vsevolodganin.clicktrack.utils.flow.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

@ActivityScoped
class ExportEpic @Inject constructor(
    private val exportWorkLauncher: ExportWorkLauncher,
) : Epic {

    override fun act(actions: Flow<Action>): Flow<Action> {
        return merge(
            actions.filterIsInstance<ExportAction.Start>()
                .consumeEach {
                    exportWorkLauncher.launchExportToAudioFile(it.clickTrackId)
                },
            actions.filterIsInstance<ExportAction.Stop>()
                .consumeEach {
                    exportWorkLauncher.stopExportToAudioFile(it.clickTrackId)
                },
        )
    }
}
