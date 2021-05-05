package com.vsevolodganin.clicktrack.view.widget

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.icons.ClickTrackIcons
import com.vsevolodganin.clicktrack.icons.clicktrackicons.Notes
import com.vsevolodganin.clicktrack.icons.clicktrackicons.notes.DisplacedEighth
import com.vsevolodganin.clicktrack.icons.clicktrackicons.notes.DisplacedHalf
import com.vsevolodganin.clicktrack.icons.clicktrackicons.notes.DisplacedQuarter
import com.vsevolodganin.clicktrack.icons.clicktrackicons.notes.DisplacedSixteenth
import com.vsevolodganin.clicktrack.icons.clicktrackicons.notes.DisplacedThirtySecond
import com.vsevolodganin.clicktrack.icons.clicktrackicons.notes.Eighth
import com.vsevolodganin.clicktrack.icons.clicktrackicons.notes.EighthQuintuplet
import com.vsevolodganin.clicktrack.icons.clicktrackicons.notes.EighthSeptuplet
import com.vsevolodganin.clicktrack.icons.clicktrackicons.notes.EighthTriplet
import com.vsevolodganin.clicktrack.icons.clicktrackicons.notes.Half
import com.vsevolodganin.clicktrack.icons.clicktrackicons.notes.HalfTriplet
import com.vsevolodganin.clicktrack.icons.clicktrackicons.notes.Quarter
import com.vsevolodganin.clicktrack.icons.clicktrackicons.notes.QuarterQuintuplet
import com.vsevolodganin.clicktrack.icons.clicktrackicons.notes.QuarterSeptuplet
import com.vsevolodganin.clicktrack.icons.clicktrackicons.notes.QuarterTriplet
import com.vsevolodganin.clicktrack.icons.clicktrackicons.notes.Sixteenth
import com.vsevolodganin.clicktrack.icons.clicktrackicons.notes.SixteenthQuintuplet
import com.vsevolodganin.clicktrack.icons.clicktrackicons.notes.SixteenthSeptuplet
import com.vsevolodganin.clicktrack.icons.clicktrackicons.notes.SixteenthTriplet
import com.vsevolodganin.clicktrack.icons.clicktrackicons.notes.ThirtySecond
import com.vsevolodganin.clicktrack.icons.clicktrackicons.notes.ThirtySecondQuintuplet
import com.vsevolodganin.clicktrack.icons.clicktrackicons.notes.ThirtySecondSeptuplet
import com.vsevolodganin.clicktrack.icons.clicktrackicons.notes.ThirtySecondTriplet
import com.vsevolodganin.clicktrack.icons.clicktrackicons.notes.Whole
import com.vsevolodganin.clicktrack.lib.Cue
import com.vsevolodganin.clicktrack.lib.NotePattern
import com.vsevolodganin.clicktrack.lib.TimeSignature
import com.vsevolodganin.clicktrack.view.preview.PREVIEW_CLICK_TRACK_1

@Composable
fun SubdivisionsChooser(
    cue: Cue,
    onSubdivisionChoose: (NotePattern) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pattern = cue.pattern
    when (cue.timeSignature.noteValue) {
        1 -> WholeNoteLayout(pattern, onSubdivisionChoose, modifier)
        in 2..3 -> HalfNoteLayout(pattern, onSubdivisionChoose, modifier)
        in 4..7 -> QuarterNoteLayout(pattern, onSubdivisionChoose, modifier)
        in 8..15 -> EighthNoteLayout(pattern, onSubdivisionChoose, modifier)
        in 16..31 -> SixteenthNoteLayout(pattern, onSubdivisionChoose, modifier)
        in 32..Int.MAX_VALUE -> ThirtySecondNoteLayout(pattern, onSubdivisionChoose, modifier)
        else -> error("Non-positive note value")
    }
}

@Composable
private fun WholeNoteLayout(pattern: NotePattern, onSubdivisionChoose: (NotePattern) -> Unit, modifier: Modifier) {
    Column(modifier = modifier) {
        val rowModifier = Modifier.fillMaxWidth()

        Row(modifier = rowModifier) {
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.STRAIGHT_X1,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.Whole,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.STRAIGHT_X2,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.Half,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.STRAIGHT_X4,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.Quarter,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.STRAIGHT_X8,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.Eighth,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.STRAIGHT_X16,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.Sixteenth,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.STRAIGHT_X32,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.ThirtySecond,
            )
        }
        Row(modifier = rowModifier) {
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.TRIPLET_X1,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.HalfTriplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.TRIPLET_X2,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.QuarterTriplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.TRIPLET_X4,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.EighthTriplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.TRIPLET_X8,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.SixteenthTriplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.TRIPLET_X16,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.ThirtySecondTriplet,
            )
        }
        Row(modifier = rowModifier) {
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.QUINTUPLET_X1,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.QuarterQuintuplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.QUINTUPLET_X2,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.EighthQuintuplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.QUINTUPLET_X4,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.SixteenthQuintuplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.QUINTUPLET_X8,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.ThirtySecondQuintuplet,
            )
        }
        Row(modifier = rowModifier) {
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.SEPTUPLET_X1,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.QuarterSeptuplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.SEPTUPLET_X2,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.EighthSeptuplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.SEPTUPLET_X4,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.SixteenthSeptuplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.SEPTUPLET_X8,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.ThirtySecondSeptuplet,
            )
        }
        Row(modifier = rowModifier) {
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.DISPLACED_X1,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.DisplacedHalf,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.DISPLACED_X2,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.DisplacedQuarter,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.DISPLACED_X4,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.DisplacedEighth,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.DISPLACED_X8,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.DisplacedSixteenth,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.DISPLACED_X16,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.DisplacedThirtySecond,
            )
        }
    }
}

@Composable
private fun HalfNoteLayout(pattern: NotePattern, onSubdivisionChoose: (NotePattern) -> Unit, modifier: Modifier) {
    Column(modifier = modifier) {
        val rowModifier = Modifier.fillMaxWidth()

        Row(modifier = rowModifier) {
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.STRAIGHT_X1,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.Half,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.STRAIGHT_X2,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.Quarter,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.STRAIGHT_X4,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.Eighth,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.STRAIGHT_X8,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.Sixteenth,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.STRAIGHT_X16,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.ThirtySecond,
            )
        }
        Row(modifier = rowModifier) {
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.TRIPLET_X1,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.HalfTriplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.TRIPLET_X2,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.QuarterTriplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.TRIPLET_X4,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.EighthTriplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.TRIPLET_X8,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.SixteenthTriplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.TRIPLET_X16,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.ThirtySecondTriplet,
            )
        }
        Row(modifier = rowModifier) {
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.QUINTUPLET_X1,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.QuarterQuintuplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.QUINTUPLET_X2,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.EighthQuintuplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.QUINTUPLET_X4,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.SixteenthQuintuplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.QUINTUPLET_X8,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.ThirtySecondQuintuplet,
            )
        }
        Row(modifier = rowModifier) {
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.SEPTUPLET_X1,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.QuarterSeptuplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.SEPTUPLET_X2,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.EighthSeptuplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.SEPTUPLET_X4,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.SixteenthSeptuplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.SEPTUPLET_X8,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.ThirtySecondSeptuplet,
            )
        }
        Row(modifier = rowModifier) {
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.DISPLACED_X1,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.DisplacedHalf,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.DISPLACED_X2,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.DisplacedQuarter,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.DISPLACED_X4,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.DisplacedEighth,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.DISPLACED_X8,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.DisplacedSixteenth,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.DISPLACED_X16,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.DisplacedThirtySecond,
            )
        }
    }
}

@Composable
private fun QuarterNoteLayout(pattern: NotePattern, onSubdivisionChoose: (NotePattern) -> Unit, modifier: Modifier) {
    Column(modifier = modifier) {
        val rowModifier = Modifier.fillMaxWidth()

        Row(modifier = rowModifier) {
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.STRAIGHT_X1,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.Quarter,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.STRAIGHT_X2,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.Eighth,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.STRAIGHT_X4,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.Sixteenth,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.STRAIGHT_X8,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.ThirtySecond,
            )
        }
        Row(modifier = rowModifier) {
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.TRIPLET_X1,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.QuarterTriplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.TRIPLET_X2,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.EighthTriplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.TRIPLET_X4,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.SixteenthTriplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.TRIPLET_X8,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.ThirtySecondTriplet,
            )
        }
        Row(modifier = rowModifier) {
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.QUINTUPLET_X1,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.QuarterQuintuplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.QUINTUPLET_X2,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.EighthQuintuplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.QUINTUPLET_X4,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.SixteenthQuintuplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.QUINTUPLET_X8,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.ThirtySecondQuintuplet,
            )
        }
        Row(modifier = rowModifier) {
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.SEPTUPLET_X1,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.QuarterSeptuplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.SEPTUPLET_X2,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.EighthSeptuplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.SEPTUPLET_X4,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.SixteenthSeptuplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.SEPTUPLET_X8,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.ThirtySecondSeptuplet,
            )
        }
        Row(modifier = rowModifier) {
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.DISPLACED_X1,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.DisplacedQuarter,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.DISPLACED_X2,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.DisplacedEighth,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.DISPLACED_X4,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.DisplacedSixteenth,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.DISPLACED_X8,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.DisplacedThirtySecond,
            )
        }
    }
}

@Composable
private fun EighthNoteLayout(pattern: NotePattern, onSubdivisionChoose: (NotePattern) -> Unit, modifier: Modifier) {
    Column(modifier = modifier) {
        val rowModifier = Modifier.fillMaxWidth()

        Row(modifier = rowModifier) {
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.STRAIGHT_X1,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.Eighth,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.STRAIGHT_X2,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.Sixteenth,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.STRAIGHT_X4,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.ThirtySecond,
            )
        }
        Row(modifier = rowModifier) {
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.TRIPLET_X1,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.EighthTriplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.TRIPLET_X2,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.SixteenthTriplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.TRIPLET_X4,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.ThirtySecondTriplet,
            )
        }
        Row(modifier = rowModifier) {
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.QUINTUPLET_X1,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.EighthQuintuplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.QUINTUPLET_X2,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.SixteenthQuintuplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.QUINTUPLET_X4,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.ThirtySecondQuintuplet,
            )
        }
        Row(modifier = rowModifier) {
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.SEPTUPLET_X1,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.EighthSeptuplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.SEPTUPLET_X2,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.SixteenthSeptuplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.SEPTUPLET_X4,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.ThirtySecondSeptuplet,
            )
        }
        Row(modifier = rowModifier) {
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.DISPLACED_X1,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.DisplacedEighth,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.DISPLACED_X2,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.DisplacedSixteenth,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.DISPLACED_X4,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.DisplacedThirtySecond,
            )
        }
    }
}

@Composable
private fun SixteenthNoteLayout(pattern: NotePattern, onSubdivisionChoose: (NotePattern) -> Unit, modifier: Modifier) {
    Column(modifier = modifier) {
        val rowModifier = Modifier.fillMaxWidth()

        Row(modifier = rowModifier) {
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.STRAIGHT_X1,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.Sixteenth,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.STRAIGHT_X2,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.ThirtySecond,
            )
        }
        Row(modifier = rowModifier) {
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.TRIPLET_X1,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.SixteenthTriplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.TRIPLET_X2,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.ThirtySecondTriplet,
            )
        }
        Row(modifier = rowModifier) {
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.QUINTUPLET_X1,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.SixteenthQuintuplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.QUINTUPLET_X2,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.ThirtySecondQuintuplet,
            )
        }
        Row(modifier = rowModifier) {
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.SEPTUPLET_X1,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.SixteenthSeptuplet,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.SEPTUPLET_X2,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.ThirtySecondSeptuplet,
            )
        }
        Row(modifier = rowModifier) {
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.DISPLACED_X1,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.DisplacedSixteenth,
            )
            SubdivisionItem(
                actualPattern = pattern,
                expectedPattern = NotePattern.DISPLACED_X2,
                onSubdivisionChoose = onSubdivisionChoose,
                imageVector = ClickTrackIcons.Notes.DisplacedThirtySecond,
            )
        }
    }
}

@Composable
private fun ThirtySecondNoteLayout(pattern: NotePattern, onSubdivisionChoose: (NotePattern) -> Unit, modifier: Modifier) {
    Row(modifier = modifier.fillMaxWidth()) {
        SubdivisionItem(
            actualPattern = pattern,
            expectedPattern = NotePattern.STRAIGHT_X1,
            onSubdivisionChoose = onSubdivisionChoose,
            imageVector = ClickTrackIcons.Notes.ThirtySecond,
        )
        SubdivisionItem(
            actualPattern = pattern,
            expectedPattern = NotePattern.TRIPLET_X1,
            onSubdivisionChoose = onSubdivisionChoose,
            imageVector = ClickTrackIcons.Notes.ThirtySecondTriplet,
        )
        SubdivisionItem(
            actualPattern = pattern,
            expectedPattern = NotePattern.QUINTUPLET_X1,
            onSubdivisionChoose = onSubdivisionChoose,
            imageVector = ClickTrackIcons.Notes.ThirtySecondQuintuplet,
        )
        SubdivisionItem(
            actualPattern = pattern,
            expectedPattern = NotePattern.SEPTUPLET_X1,
            onSubdivisionChoose = onSubdivisionChoose,
            imageVector = ClickTrackIcons.Notes.ThirtySecondSeptuplet,
        )
        SubdivisionItem(
            actualPattern = pattern,
            expectedPattern = NotePattern.DISPLACED_X1,
            onSubdivisionChoose = onSubdivisionChoose,
            imageVector = ClickTrackIcons.Notes.DisplacedThirtySecond,
        )
    }
}

@Composable
private fun RowScope.SubdivisionItem(
    actualPattern: NotePattern,
    expectedPattern: NotePattern,
    onSubdivisionChoose: (NotePattern) -> Unit,
    imageVector: ImageVector,
) {
    SubdivisionItem(
        onClick = { onSubdivisionChoose(expectedPattern) },
        isSelected = actualPattern == expectedPattern,
        imageVector = imageVector,
    )
}

@Composable
private fun RowScope.SubdivisionItem(
    onClick: () -> Unit,
    isSelected: Boolean,
    imageVector: ImageVector,
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .padding(2.dp)
            .border(
                width = 2.dp,
                color = if (isSelected) {
                    MaterialTheme.colors.secondary
                } else {
                    MaterialTheme.colors.primary
                },
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(8.dp),
    ) {
        Icon(
            imageVector = imageVector,
            modifier = Modifier.align(Alignment.Center),
            contentDescription = null
        )
    }
}

@Preview
@Composable
private fun Preview() {
    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
    ) {
        for (noteDuration in arrayOf(1, 2, 4, 8, 16, 32)) {
            var cue by remember {
                mutableStateOf(PREVIEW_CLICK_TRACK_1.value.cues[0].copy(
                    timeSignature = TimeSignature(4, noteDuration)
                ))
            }
            SubdivisionsChooser(
                cue = cue,
                onSubdivisionChoose = { cue = cue.copy(pattern = it) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}