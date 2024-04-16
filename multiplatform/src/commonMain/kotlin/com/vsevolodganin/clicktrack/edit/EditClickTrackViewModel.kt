package com.vsevolodganin.clicktrack.edit

import com.vsevolodganin.clicktrack.model.CueDuration
import com.vsevolodganin.clicktrack.model.NotePattern
import com.vsevolodganin.clicktrack.model.TimeSignature
import kotlinx.coroutines.flow.StateFlow

interface EditClickTrackViewModel {
    val state: StateFlow<EditClickTrackState?>
    fun onBackClick()
    fun onForwardClick()
    fun onNameChange(name: String)
    fun onLoopChange(loop: Boolean)
    fun onTempoDiffIncrementClick()
    fun onTempoDiffDecrementClick()
    fun onAddNewCueClick()
    fun onCueRemove(index: Int)
    fun onCueNameChange(index: Int, name: String)
    fun onCueBpmChange(index: Int, bpm: Int)
    fun onCueTimeSignatureChange(index: Int, timeSignature: TimeSignature)
    fun onCueDurationChange(index: Int, duration: CueDuration)
    fun onCueDurationTypeChange(index: Int, durationType: CueDuration.Type)
    fun onCuePatternChange(index: Int, pattern: NotePattern)
    fun onItemMove(from: Int, to: Int)
    fun onItemMoveFinished()
}
