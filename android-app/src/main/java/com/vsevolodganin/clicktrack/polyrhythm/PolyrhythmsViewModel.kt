package com.vsevolodganin.clicktrack.polyrhythm

import kotlinx.coroutines.flow.StateFlow

interface PolyrhythmsViewModel {
    val state: StateFlow<PolyrhythmsState?>
    fun onBackClick()
    fun onTogglePlay()
    fun onLayer1Change(value: Int)
    fun onLayer2Change(value: Int)
}
