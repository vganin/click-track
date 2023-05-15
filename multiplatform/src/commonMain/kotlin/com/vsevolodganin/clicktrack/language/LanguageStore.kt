package com.vsevolodganin.clicktrack.language

import kotlinx.coroutines.flow.MutableStateFlow

interface LanguageStore {
    val appLanguage: MutableStateFlow<AppLanguage>
}
