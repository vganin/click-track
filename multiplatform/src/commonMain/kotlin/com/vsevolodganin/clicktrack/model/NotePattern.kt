package com.vsevolodganin.clicktrack.model

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.vsevolodganin.clicktrack.model.NotePatternGroup.DISPLACED
import com.vsevolodganin.clicktrack.model.NotePatternGroup.QUINTUPLET
import com.vsevolodganin.clicktrack.model.NotePatternGroup.SEPTUPLET
import com.vsevolodganin.clicktrack.model.NotePatternGroup.STRAIGHT
import com.vsevolodganin.clicktrack.model.NotePatternGroup.TRIPLET
import com.vsevolodganin.clicktrack.utils.math.Rational
import com.vsevolodganin.clicktrack.utils.math.over
import kotlinx.serialization.Serializable

enum class NotePatternGroup {
    STRAIGHT,
    TRIPLET,
    QUINTUPLET,
    SEPTUPLET,
    DISPLACED
}

@Serializable
@Parcelize
enum class NotePattern(val events: List<NoteEvent>, val group: NotePatternGroup) : Parcelable {

    STRAIGHT_X1(straightPattern(1), STRAIGHT),
    STRAIGHT_X2(straightPattern(2), STRAIGHT),
    STRAIGHT_X4(straightPattern(4), STRAIGHT),
    STRAIGHT_X8(straightPattern(8), STRAIGHT),
    STRAIGHT_X16(straightPattern(16), STRAIGHT),
    STRAIGHT_X32(straightPattern(32), STRAIGHT),

    TRIPLET_X1(tripletPattern(1), TRIPLET),
    TRIPLET_X2(tripletPattern(2), TRIPLET),
    TRIPLET_X4(tripletPattern(4), TRIPLET),
    TRIPLET_X8(tripletPattern(8), TRIPLET),
    TRIPLET_X16(tripletPattern(16), TRIPLET),

    QUINTUPLET_X1(quintupletPattern(1), QUINTUPLET),
    QUINTUPLET_X2(quintupletPattern(2), QUINTUPLET),
    QUINTUPLET_X4(quintupletPattern(4), QUINTUPLET),
    QUINTUPLET_X8(quintupletPattern(8), QUINTUPLET),

    SEPTUPLET_X1(septupletPattern(1), SEPTUPLET),
    SEPTUPLET_X2(septupletPattern(2), SEPTUPLET),
    SEPTUPLET_X4(septupletPattern(4), SEPTUPLET),
    SEPTUPLET_X8(septupletPattern(8), SEPTUPLET),

    DISPLACED_X1(displacedPattern(1), DISPLACED),
    DISPLACED_X2(displacedPattern(2), DISPLACED),
    DISPLACED_X4(displacedPattern(4), DISPLACED),
    DISPLACED_X8(displacedPattern(8), DISPLACED),
    DISPLACED_X16(displacedPattern(16), DISPLACED),
}

private fun straightPattern(multiplier: Int) = filledPattern(multiplier, 1 over 1)
private fun tripletPattern(multiplier: Int) = filledPattern(multiplier, 2 over 3)
private fun quintupletPattern(multiplier: Int) = filledPattern(multiplier, 4 over 5)
private fun septupletPattern(multiplier: Int) = filledPattern(multiplier, 4 over 7)

private fun filledPattern(multiplier: Int, length: Rational) = mutableListOf<NoteEvent>().apply {
    val resultingLength = length.numerator over (length.denominator * multiplier)
    repeat(multiplier) {
        this += NoteEvent(
            length = resultingLength,
            type = NoteEvent.Type.NOTE
        )
    }
}

private fun displacedPattern(multiplier: Int) = mutableListOf<NoteEvent>().apply {
    val resultingLength = 1 over (multiplier * 2)
    repeat(multiplier) {
        this += NoteEvent(
            length = resultingLength,
            type = NoteEvent.Type.REST
        )
        this += NoteEvent(
            length = resultingLength,
            type = NoteEvent.Type.NOTE
        )
    }
}
