package com.vsevolodganin.clicktrack.state.reducer

import com.vsevolodganin.clicktrack.lib.Cue
import com.vsevolodganin.clicktrack.lib.applyDiff
import com.vsevolodganin.clicktrack.lib.bpm
import com.vsevolodganin.clicktrack.model.ClickTrackId
import com.vsevolodganin.clicktrack.model.ClickTrackProgress
import com.vsevolodganin.clicktrack.redux.Action
import com.vsevolodganin.clicktrack.state.actions.ClickTrackAction
import com.vsevolodganin.clicktrack.state.actions.MetronomeAction
import com.vsevolodganin.clicktrack.state.screen.MetronomeScreenState
import com.vsevolodganin.clicktrack.state.screen.Screen

fun Screen.Metronome.reduceMetronome(action: Action): Screen {
    return Screen.Metronome(
        state = state.reduce(action)?.limitBpm()
    )
}

@JvmName("reduceNullable")
private fun MetronomeScreenState?.reduce(action: Action): MetronomeScreenState? {
    return when (action) {
        is MetronomeAction.SetScreenState -> action.state
        else -> this?.reduce(action)
    }
}

@JvmName("reduceNonNull")
private fun MetronomeScreenState.reduce(action: Action): MetronomeScreenState {
    return when (action) {
        is MetronomeAction.SetScreenState -> action.state
        is MetronomeAction.SetBpm -> updateCue { it.copy(bpm = action.bpm) }
        is MetronomeAction.ChangeBpm -> updateCue { it.copy(bpm = it.bpm.applyDiff(action.by)) }
        is MetronomeAction.SetPattern -> updateCue { it.copy(pattern = action.pattern) }
        is MetronomeAction.ToggleOptions -> copy(areOptionsExpanded = !areOptionsExpanded)
        is MetronomeAction.OpenOptions -> copy(areOptionsExpanded = true)
        is MetronomeAction.CloseOptions -> copy(areOptionsExpanded = false)
        is ClickTrackAction.UpdateCurrentlyPlaying -> if (action.playbackState?.clickTrack?.id == ClickTrackId.Builtin.METRONOME) {
            copy(progress = ClickTrackProgress(action.playbackState.progress), isPlaying = true)
        } else {
            copy(progress = null, isPlaying = false)
        }
        else -> this
    }
}

private fun MetronomeScreenState.updateCue(update: (Cue) -> Cue): MetronomeScreenState {
    return copy(clickTrack = clickTrack.run {
        copy(value = value.copy(cues = value.cues.map { update(it) }))
    })
}

private fun MetronomeScreenState.limitBpm(): MetronomeScreenState {
    return updateCue {
        it.copy(bpm = it.bpm.coerceIn(BPM_RANGE))
    }
}

private val BPM_RANGE = 1.bpm..999.bpm
