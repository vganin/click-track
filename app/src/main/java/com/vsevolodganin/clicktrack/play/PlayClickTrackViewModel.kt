package com.vsevolodganin.clicktrack.play

import kotlinx.coroutines.flow.StateFlow

interface PlayClickTrackViewModel {
    val state: StateFlow<PlayClickTrackState?>
    fun onBackClick()
    fun onTogglePlay()
    fun onTogglePlayTrackingMode()
    fun onProgressDragStart()
    fun onProgressDrop(progress: Double)
    fun onEditClick()
    fun onRemoveClick()
    fun onExportClick()
    fun onCancelExportClick()
}
