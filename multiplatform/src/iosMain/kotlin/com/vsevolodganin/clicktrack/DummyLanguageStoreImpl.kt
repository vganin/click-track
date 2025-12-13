package com.vsevolodganin.clicktrack

import com.vsevolodganin.clicktrack.di.component.MainControllerScope
import com.vsevolodganin.clicktrack.language.AppLanguage
import com.vsevolodganin.clicktrack.language.LanguageStore
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.MutableStateFlow

@SingleIn(MainControllerScope::class)
@Inject
class DummyLanguageStoreImpl : LanguageStore {
    override val appLanguage: MutableStateFlow<AppLanguage> = MutableStateFlow(AppLanguage.ENGLISH)
}
