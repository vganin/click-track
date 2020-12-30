package com.vsevolodganin.clicktrack.storage

import android.content.SharedPreferences
import androidx.core.content.edit
import com.vsevolodganin.clicktrack.di.component.ApplicationScoped
import com.vsevolodganin.clicktrack.di.module.UserPreferences
import com.vsevolodganin.clicktrack.lib.BeatsPerMinute
import com.vsevolodganin.clicktrack.lib.bpm
import javax.inject.Inject

@ApplicationScoped
class UserPreferencesRepository @Inject constructor(
    @UserPreferences private val sharedPreferences: SharedPreferences,
) {
    var metronomeBpm: BeatsPerMinute
        set(value) {
            sharedPreferences.edit { putInt(Key.METRONOME_BPM, value.value) }
        }
        get() {
            return sharedPreferences.getInt(Key.METRONOME_BPM, 60).bpm
        }

    private object Key {
        const val METRONOME_BPM = "metronome_bpm"
    }
}
