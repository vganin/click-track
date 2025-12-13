package com.vsevolodganin.clicktrack.di.component

import com.vsevolodganin.clicktrack.export.ExportWorker
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class ExportWorkerScope

@GraphExtension(ExportWorkerScope::class)
interface ExportWorkerComponent {

    fun inject(exportWorker: ExportWorker)

    @GraphExtension.Factory
    fun interface Factory {
        fun create(): ExportWorkerComponent
    }
}
