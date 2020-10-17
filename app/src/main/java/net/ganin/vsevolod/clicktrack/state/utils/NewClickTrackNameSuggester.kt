package net.ganin.vsevolod.clicktrack.state.utils

import net.ganin.vsevolod.clicktrack.storage.ClickTrackRepository

class NewClickTrackNameSuggester(private val storage: ClickTrackRepository) {

    fun suggest(): String {
        val maxUsedDefaultNameNumber = storage.getAllNames()
            .asSequence()
            .mapNotNull(::findDefaultNameNumber)
            .maxOrNull()
            ?: 0
        return format(maxUsedDefaultNameNumber + 1)
    }

    private fun findDefaultNameNumber(input: String): Int? {
        return DEFAULT_NAME_REGEXP.find(input)?.groupValues?.get(1)?.toIntOrNull()
    }

    private companion object {
        const val DEFAULT_NAME_TEMPLATE = "Unnamed click track"
        val DEFAULT_NAME_REGEXP = "$DEFAULT_NAME_TEMPLATE (\\d*)?".toRegex()
        fun format(withNumber: Int): String = "$DEFAULT_NAME_TEMPLATE $withNumber"
    }
}
