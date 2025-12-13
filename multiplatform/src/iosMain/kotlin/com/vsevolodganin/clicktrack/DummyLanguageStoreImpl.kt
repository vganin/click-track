package com.vsevolodganin.clicktrack

import com.vsevolodganin.clicktrack.di.component.MainControllerScope
import com.vsevolodganin.clicktrack.language.AppLanguage
import com.vsevolodganin.clicktrack.language.LanguageStore
import kotlinx.coroutines.flow.MutableStateFlow
import dev.zacsweers.metro.Inject

@MainControllerScope
@Inject
class DummyLanguageStoreImpl : LanguageStore {
    override val appLanguage: MutableStateFlow<AppLanguage> = MutableStateFlow(AppLanguage.ENGLISH)
}
