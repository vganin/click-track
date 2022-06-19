package com.vsevolodganin.clicktrack.state.logic

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.os.Build
import com.vsevolodganin.clicktrack.di.component.ActivityScoped
import com.vsevolodganin.clicktrack.export.ExportState
import com.vsevolodganin.clicktrack.export.ExportToAudioFile
import com.vsevolodganin.clicktrack.lib.ClickTrack
import com.vsevolodganin.clicktrack.utils.android.PermissionsHelper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityScoped
class ClickTrackExporter @Inject constructor(
    private val audioExporter: ExportToAudioFile,
    private val mediaStoreAccess: MediaStoreAccess,
    private val permissionsHelper: PermissionsHelper,
) {
    private val state = MutableStateFlow<ExportState?>(null)
    private var job: Job? = null

    fun start(clickTrack: ClickTrack) {
        job?.cancel()
        job = GlobalScope.launch {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P && !permissionsHelper.requestPermission(WRITE_EXTERNAL_STORAGE)) {
                return@launch
            }

            state.value = ExportState(0f)

            audioExporter.export(
                clickTrack = clickTrack,
                onProgress = {
                    if (!isActive) {
                        state.value = ExportState(it)
                    }
                },
                onFinished = {
                    state.value = null
                }
            )?.let(mediaStoreAccess::addAudioFile)
        }
    }

    fun cancel() {
        state.value = null
        job?.cancel()
    }

    fun state(): Flow<ExportState?> = state
}
