package com.vsevolodganin.clicktrack.storage

import android.content.SharedPreferences
import com.vsevolodganin.clicktrack.di.component.ApplicationScoped
import com.vsevolodganin.clicktrack.di.module.UserPreferences
import com.vsevolodganin.clicktrack.lib.BeatsPerMinute
import com.vsevolodganin.clicktrack.lib.NotePattern
import com.vsevolodganin.clicktrack.lib.bpm
import com.vsevolodganin.clicktrack.sounds.model.BuiltinClickSounds
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundsId
import com.vsevolodganin.clicktrack.theme.Theme
import com.vsevolodganin.clicktrack.utils.android.sharedprefs.IntConvertibleProperty
import com.vsevolodganin.clicktrack.utils.android.sharedprefs.IntProperty
import com.vsevolodganin.clicktrack.utils.android.sharedprefs.SharedPreferencesProperty
import com.vsevolodganin.clicktrack.utils.android.sharedprefs.StringConvertibleProperty
import javax.inject.Inject
import kotlin.reflect.KProperty
import kotlinx.coroutines.flow.Flow

@ApplicationScoped
class UserPreferencesRepository @Inject constructor(
    @UserPreferences private val sharedPreferences: SharedPreferences,
) {
    var appVersionCode: Int by Properties.APP_VERSION_CODE
    var metronomeBpm: BeatsPerMinute by Properties.METRONOME_BPM
    var metronomePattern: NotePattern by Properties.METRONOME_PATTERN
    var theme: Theme by Properties.THEME
    var selectedSoundsId: ClickSoundsId by Properties.SELECTED_SOUNDS

    val selectedSoundsIdFlow: Flow<ClickSoundsId> = Properties.SELECTED_SOUNDS.flow()

    private operator fun <T> SharedPreferencesProperty<T>.getValue(thisObj: Any?, property: KProperty<*>): T {
        return sharedPreferences.getter()
    }

    private operator fun <T> SharedPreferencesProperty<T>.setValue(thisObj: Any?, property: KProperty<*>, value: T) {
        sharedPreferences.setter(value)
    }

    private fun <T> SharedPreferencesProperty<T>.flow() = sharedPreferences.flow()

    private object Properties {

        val APP_VERSION_CODE = IntProperty(
            key = "app_version_code",
            defaultValue = Const.NO_APP_VERSION_CODE,
        )

        val METRONOME_BPM = IntConvertibleProperty(
            key = "metronome_bpm",
            defaultValue = 120.bpm,
            toInt = { value },
            fromInt = { bpm }
        )

        val METRONOME_PATTERN = StringConvertibleProperty(
            key = "metronome_pattern",
            defaultValue = NotePattern.STRAIGHT_X1,
            toString = { toString() },
            fromString = { NotePattern.valueOf(this) }
        )

        val THEME = StringConvertibleProperty(
            key = "theme",
            defaultValue = Theme.SYSTEM,
            toString = { toString() },
            fromString = { Theme.valueOf(this) }
        )

        val SELECTED_SOUNDS = StringConvertibleProperty(
            key = "selected_sounds_id",
            defaultValue = ClickSoundsId.Builtin(BuiltinClickSounds.BEEP),
            toString = {
                when (this) {
                    is ClickSoundsId.Database -> value.toString()
                    is ClickSoundsId.Builtin -> value.storageKey
                }
            },
            fromString = {
                toLongOrNull()?.let(ClickSoundsId::Database)
                    ?: BuiltinClickSounds.values().firstOrNull { it.storageKey == this }?.let(ClickSoundsId::Builtin)
                    ?: ClickSoundsId.Builtin(BuiltinClickSounds.BEEP)
            },
        )
    }

    object Const {
        const val NO_APP_VERSION_CODE = -1
    }
}
