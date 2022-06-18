package com.vsevolodganin.clicktrack.state.logic

import com.vsevolodganin.clicktrack.di.component.ViewModelScoped
import com.vsevolodganin.clicktrack.export.ExportState
import com.vsevolodganin.clicktrack.export.ExportToAudioFile
import com.vsevolodganin.clicktrack.lib.ClickTrack
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ViewModelScoped
class ClickTrackExporter @Inject constructor(
    private val audioExporter: ExportToAudioFile,
    private val mediaStoreAccess: MediaStoreAccess,
) {
    private val state = MutableStateFlow<ExportState?>(null)
    private var job: Job? = null

    fun start(clickTrack: ClickTrack) {
        state.value = ExportState(0f)
        job?.cancel()
        job = GlobalScope.launch {
            audioExporter.export(
                clickTrack = clickTrack,
                onProgress = {
                    state.value = ExportState(it)
                },
                onFinished = {
                    state.value = null
                }
            )?.let(mediaStoreAccess::addFile)
        }
    }

    fun cancel() {
        state.value = null
        job?.cancel()
    }

    fun state(): Flow<ExportState?> = state
}
