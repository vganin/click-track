package com.vsevolodganin.clicktrack.language

import android.os.Build
import java.util.Locale

val LOCALE_EN: Locale = Locale.ENGLISH
val LOCALE_RU: Locale = createLocale("ru")

private fun createLocale(language: String): Locale {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
        Locale.of(language)
    } else {
        @Suppress("DEPRECATION")
        Locale(language)
    }
}
