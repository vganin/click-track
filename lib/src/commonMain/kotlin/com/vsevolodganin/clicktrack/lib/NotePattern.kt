package com.vsevolodganin.clicktrack.lib

import com.vsevolodganin.clicktrack.lib.android.AndroidParcelable
import com.vsevolodganin.clicktrack.lib.android.AndroidParcelize
import com.vsevolodganin.clicktrack.lib.math.Rational
import com.vsevolodganin.clicktrack.lib.math.over
import kotlinx.serialization.Serializable

@Serializable
@AndroidParcelize
public enum class NotePattern(public val events: List<NoteEvent>) : AndroidParcelable {

    STRAIGHT_X1(straightPattern(1)),
    STRAIGHT_X2(straightPattern(2)),
    STRAIGHT_X4(straightPattern(4)),
    STRAIGHT_X8(straightPattern(8)),
    STRAIGHT_X16(straightPattern(16)),
    STRAIGHT_X32(straightPattern(32)),

    TRIPLET_X1(tripletPattern(1)),
    TRIPLET_X2(tripletPattern(2)),
    TRIPLET_X4(tripletPattern(4)),
    TRIPLET_X8(tripletPattern(8)),
    TRIPLET_X16(tripletPattern(16)),

    QUINTUPLET_X1(quintupletPattern(1)),
    QUINTUPLET_X2(quintupletPattern(2)),
    QUINTUPLET_X4(quintupletPattern(4)),
    QUINTUPLET_X8(quintupletPattern(8)),

    SEPTUPLET_X1(septupletPattern(1)),
    SEPTUPLET_X2(septupletPattern(2)),
    SEPTUPLET_X4(septupletPattern(4)),
    SEPTUPLET_X8(septupletPattern(8)),

    DISPLACED_X1(displacedPattern(1)),
    DISPLACED_X2(displacedPattern(2)),
    DISPLACED_X4(displacedPattern(4)),
    DISPLACED_X8(displacedPattern(8)),
    DISPLACED_X16(displacedPattern(16)),
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
