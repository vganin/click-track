package com.vsevolodganin.clicktrack.common

import com.vsevolodganin.clicktrack.storage.ClickTrackRepository
import dagger.Reusable
import javax.inject.Inject

@Reusable
class NewClickTrackNameSuggester @Inject constructor(
    private val storage: ClickTrackRepository
) {
    fun suggest(baseName: String): String {
        val maxUsedDefaultNameNumber = storage.getAllNames()
            .asSequence()
            .mapNotNull { findDefaultNameNumber(baseName, it) }
            .maxOrNull()
            ?: 0
        return format(baseName, maxUsedDefaultNameNumber + 1)
    }

    private fun findDefaultNameNumber(baseName: String, input: String): Int? {
        return "$baseName (\\d*)?".toRegex().find(input)?.groupValues?.get(1)?.toIntOrNull()
    }

    private fun format(baseName: String, withNumber: Int) = "$baseName $withNumber"
}