package com.vsevolodganin.clicktrack.metronome

import com.vsevolodganin.clicktrack.model.BeatsPerMinuteOffset
import com.vsevolodganin.clicktrack.model.NotePattern
import kotlinx.coroutines.flow.StateFlow

interface MetronomeViewModel {
    val state: StateFlow<MetronomeState?>
    fun onBackClick()
    fun onToggleOptions()
    fun onOptionsExpandedChange(isOpened: Boolean)
    fun onPatternChoose(pattern: NotePattern)
    fun onBpmChange(bpmDiff: BeatsPerMinuteOffset)
    fun onTogglePlay()
    fun onBpmMeterClick()
}
