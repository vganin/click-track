package com.vsevolodganin.clicktrack.lib

import com.vsevolodganin.clicktrack.lib.math.Rational
import com.vsevolodganin.clicktrack.lib.math.ZERO
import com.vsevolodganin.clicktrack.lib.math.compareTo
import com.vsevolodganin.clicktrack.lib.math.lcm
import com.vsevolodganin.clicktrack.lib.math.min
import com.vsevolodganin.clicktrack.lib.math.minus
import com.vsevolodganin.clicktrack.lib.math.plus
import com.vsevolodganin.clicktrack.lib.utils.collection.toRoundRobin

/**
 * Polyrhythm is represented as a grid with the length equal to least common multiple of all input patterns.
 */
public data class Polyrhythm internal constructor(val columns: List<Column>, val untilFirst: Rational) {
    public data class Column internal constructor(val indices: List<Int>, val untilNext: Rational)
}

public fun Polyrhythm(pattern1: List<NoteEvent>, pattern2: List<NoteEvent>): Polyrhythm {
    val resultingPattern = mutableListOf<Polyrhythm.Column>()

    val commonLength = lcm(pattern1.length, pattern2.length)
    val pattern1Commonized = pattern1.resize(commonLength)
    val pattern2Commonized = pattern2.resize(commonLength)
    val pattern1InitialRestLength = pattern1.initialRestLength()
    val pattern2InitialRestLength = pattern2.initialRestLength()

    var pattern1RunningLength = pattern1InitialRestLength
    var pattern2RunningLength = pattern2InitialRestLength
    val pattern1NonRestLengths = pattern1Commonized.continuousNonRestLengths()
    val pattern2NonRestLengths = pattern2Commonized.continuousNonRestLengths()
    val pattern1NonRestLengthsIterator = pattern1NonRestLengths.iterator()
    val pattern2NonRestLengthsIterator = pattern2NonRestLengths.iterator()
    while (pattern1RunningLength < commonLength || pattern2RunningLength < commonLength) {
        when {
            pattern1RunningLength < pattern2RunningLength -> {
                val pattern1EventLength = pattern1NonRestLengthsIterator.next()
                resultingPattern += Polyrhythm.Column(
                    indices = listOf(0),
                    untilNext = min(pattern1EventLength, pattern2RunningLength - pattern1RunningLength)
                )
                pattern1RunningLength += pattern1EventLength
            }
            pattern2RunningLength < pattern1RunningLength -> {
                val pattern2EventLength = pattern2NonRestLengthsIterator.next()
                resultingPattern += Polyrhythm.Column(
                    indices = listOf(1),
                    untilNext = min(pattern2EventLength, pattern1RunningLength - pattern2RunningLength)
                )
                pattern2RunningLength += pattern2EventLength
            }
            else -> {
                val pattern1EventLength = pattern1NonRestLengthsIterator.next()
                val pattern2EventLength = pattern2NonRestLengthsIterator.next()
                resultingPattern += Polyrhythm.Column(
                    indices = listOf(0, 1),
                    untilNext = min(pattern1EventLength, pattern2EventLength)
                )
                pattern1RunningLength += pattern1EventLength
                pattern2RunningLength += pattern2EventLength
            }
        }
    }

    return Polyrhythm(
        columns = resultingPattern,
        untilFirst = min(pattern1InitialRestLength, pattern2InitialRestLength)
    )
}

private fun List<NoteEvent>.resize(length: Rational): List<NoteEvent> {
    if (isEmpty()) return emptyList()

    val roundSequence = toRoundRobin().iterator()
    val result = mutableListOf<NoteEvent>()

    var runningLength = Rational.ZERO
    while (runningLength < length) {
        val next = roundSequence.next()
        result += next
        runningLength += next.length
    }

    return result
}

private fun List<NoteEvent>.continuousNonRestLengths(): List<Rational> {
    val result = mutableListOf<Rational>()

    var nonRestEvent: NoteEvent? = null
    var runningLength = Rational.ZERO
    for (event in this) {
        when (event.type) {
            NoteEvent.Type.NOTE -> {
                nonRestEvent?.let { result += runningLength }
                nonRestEvent = event
                runningLength = event.length
            }
            NoteEvent.Type.REST -> {
                runningLength += event.length
            }
        }
    }
    nonRestEvent?.let { result += runningLength }

    return result
}

private fun List<NoteEvent>.initialRestLength(): Rational {
    return indexOfFirst { it.type != NoteEvent.Type.REST }.takeIf { it >= 0 }
        ?.let { indexOfFirstNonRest -> subList(0, indexOfFirstNonRest).length }
        ?: Rational.ZERO
}

private val List<NoteEvent>.length: Rational get() = fold(Rational.ZERO) { acc, event -> acc + event.length }
