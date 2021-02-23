package com.vsevolodganin.clicktrack.storage

import android.content.SharedPreferences
import com.vsevolodganin.clicktrack.di.component.ApplicationScoped
import com.vsevolodganin.clicktrack.di.module.UserPreferences
import com.vsevolodganin.clicktrack.lib.BeatsPerMinute
import com.vsevolodganin.clicktrack.lib.bpm
import com.vsevolodganin.clicktrack.theme.Theme
import com.vsevolodganin.clicktrack.utils.android.sharedprefs.IntConvertibleProperty
import com.vsevolodganin.clicktrack.utils.android.sharedprefs.SharedPreferencesProperty
import com.vsevolodganin.clicktrack.utils.android.sharedprefs.StringConvertibleProperty
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.reflect.KProperty

@ApplicationScoped
class UserPreferencesRepository @Inject constructor(
    @UserPreferences private val sharedPreferences: SharedPreferences,
) {
    var metronomeBpm: BeatsPerMinute by Properties.METRONOME_BPM
    var theme: Theme by Properties.THEME

    fun themeFlow(): Flow<Theme> = Properties.THEME.flow()

    private operator fun <T> SharedPreferencesProperty<T>.getValue(thisObj: Any?, property: KProperty<*>): T {
        return sharedPreferences.getter()
    }

    private operator fun <T> SharedPreferencesProperty<T>.setValue(thisObj: Any?, property: KProperty<*>, value: T) {
        return sharedPreferences.setter(value)
    }

    private fun <T> SharedPreferencesProperty<T>.flow() = sharedPreferences.flow()

    private object Properties {

        val METRONOME_BPM = IntConvertibleProperty(
            key = "metronome_bpm",
            defaultValue = 120.bpm,
            toInt = { value },
            fromInt = { bpm }
        )

        val THEME = StringConvertibleProperty(
            key = "theme",
            defaultValue = Theme.SYSTEM,
            toString = { toString() },
            fromString = { Theme.valueOf(this) }
        )
    }
}
