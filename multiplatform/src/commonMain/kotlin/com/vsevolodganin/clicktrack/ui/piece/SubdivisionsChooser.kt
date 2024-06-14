package com.vsevolodganin.clicktrack.ui.piece

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.generated.resources.MR
import com.vsevolodganin.clicktrack.model.NotePattern
import com.vsevolodganin.clicktrack.model.NotePatternGroup
import com.vsevolodganin.clicktrack.model.TimeSignature
import com.vsevolodganin.clicktrack.utils.compose.Preview
import dev.icerock.moko.resources.compose.painterResource

@Composable
fun SubdivisionsChooser(
    pattern: NotePattern,
    timeSignature: TimeSignature,
    onSubdivisionChoose: (NotePattern) -> Unit,
    modifier: Modifier = Modifier,
    alwaysExpanded: Boolean = false,
) {
    var expanded by remember { mutableStateOf(false) }

    val collapsingOnSubdivisionChoose: (NotePattern) -> Unit = remember {
        {
            expanded = false
            onSubdivisionChoose(it)
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val layoutModifier = Modifier.weight(1f)
        val layoutArrangement = Arrangement.spacedBy(4.dp)
        val noteValue = timeSignature.noteValue
        val reallyExpanded = alwaysExpanded || expanded

        when (noteValue) {
            1 -> WholeNoteLayout(pattern, collapsingOnSubdivisionChoose, layoutModifier, layoutArrangement, reallyExpanded)
            in 2..3 -> HalfNoteLayout(pattern, collapsingOnSubdivisionChoose, layoutModifier, layoutArrangement, reallyExpanded)
            in 4..7 -> QuarterNoteLayout(pattern, collapsingOnSubdivisionChoose, layoutModifier, layoutArrangement, reallyExpanded)
            in 8..15 -> EighthNoteLayout(pattern, collapsingOnSubdivisionChoose, layoutModifier, layoutArrangement, reallyExpanded)
            in 16..31 -> SixteenthNoteLayout(pattern, collapsingOnSubdivisionChoose, layoutModifier, layoutArrangement, reallyExpanded)
            in 32..Int.MAX_VALUE -> ThirtySecondNoteLayout(pattern, collapsingOnSubdivisionChoose, layoutModifier, layoutArrangement)
            else -> error("Non-positive note value")
        }

        AnimatedVisibility(
            visible = noteValue < 32 && !alwaysExpanded,
            enter = fadeIn() + slideInHorizontally(initialOffsetX = { it / 2 }),
            exit = fadeOut() + slideOutHorizontally(targetOffsetX = { it / 2 }),
        ) {
            IconButton(modifier = Modifier.wrapContentWidth(unbounded = true), onClick = { expanded = !expanded }) {
                ExpandableChevron(expanded)
            }
        }
    }
}

@Composable
private fun WholeNoteLayout(
    pattern: NotePattern,
    onSubdivisionChoose: (NotePattern) -> Unit,
    modifier: Modifier,
    arrangement: Arrangement.HorizontalOrVertical,
    expanded: Boolean,
) {
    Column(
        modifier = modifier,
        verticalArrangement = arrangement,
    ) {
        val rowModifier = Modifier.fillMaxWidth()

        RowAnimatedVisibility(visible = expanded || pattern.group == NotePatternGroup.STRAIGHT) {
            Row(
                modifier = rowModifier,
                horizontalArrangement = arrangement,
            ) {
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.STRAIGHT_X1,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.whole),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.STRAIGHT_X2,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.half),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.STRAIGHT_X4,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.quarter),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.STRAIGHT_X8,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.eighth),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.STRAIGHT_X16,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.sixteenth),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.STRAIGHT_X32,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.thirty_second),
                )
            }
        }

        RowAnimatedVisibility(visible = expanded || pattern.group == NotePatternGroup.TRIPLET) {
            Row(
                modifier = rowModifier,
                horizontalArrangement = arrangement,
            ) {
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.TRIPLET_X1,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.half_triplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.TRIPLET_X2,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.quarter_triplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.TRIPLET_X4,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.eighth_triplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.TRIPLET_X8,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.sixteenth_triplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.TRIPLET_X16,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.thirty_second_triplet),
                )
            }
        }

        RowAnimatedVisibility(visible = expanded || pattern.group == NotePatternGroup.QUINTUPLET) {
            Row(
                modifier = rowModifier,
                horizontalArrangement = arrangement,
            ) {
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.QUINTUPLET_X1,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.quarter_quintuplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.QUINTUPLET_X2,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.eighth_quintuplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.QUINTUPLET_X4,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.sixteenth_quintuplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.QUINTUPLET_X8,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.thirty_second_quintuplet),
                )
            }
        }

        RowAnimatedVisibility(visible = expanded || pattern.group == NotePatternGroup.SEPTUPLET) {
            Row(
                modifier = rowModifier,
                horizontalArrangement = arrangement,
            ) {
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.SEPTUPLET_X1,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.quarter_septuplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.SEPTUPLET_X2,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.eighth_septuplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.SEPTUPLET_X4,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.sixteenth_septuplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.SEPTUPLET_X8,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.thirty_second_septuplet),
                )
            }
        }

        RowAnimatedVisibility(visible = expanded || pattern.group == NotePatternGroup.DISPLACED) {
            Row(
                modifier = rowModifier,
                horizontalArrangement = arrangement,
            ) {
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.DISPLACED_X1,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.displaced_half),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.DISPLACED_X2,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.displaced_quarter),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.DISPLACED_X4,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.displaced_eighth),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.DISPLACED_X8,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.displaced_sixteenth),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.DISPLACED_X16,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.displaced_thirty_second),
                )
            }
        }
    }
}

@Composable
private fun HalfNoteLayout(
    pattern: NotePattern,
    onSubdivisionChoose: (NotePattern) -> Unit,
    modifier: Modifier,
    arrangement: Arrangement.HorizontalOrVertical,
    expanded: Boolean,
) {
    Column(
        modifier = modifier,
        verticalArrangement = arrangement,
    ) {
        val rowModifier = Modifier.fillMaxWidth()

        RowAnimatedVisibility(visible = expanded || pattern.group == NotePatternGroup.STRAIGHT) {
            Row(
                modifier = rowModifier,
                horizontalArrangement = arrangement,
            ) {
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.STRAIGHT_X1,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.half),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.STRAIGHT_X2,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.quarter),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.STRAIGHT_X4,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.eighth),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.STRAIGHT_X8,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.sixteenth),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.STRAIGHT_X16,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.thirty_second),
                )
            }
        }

        RowAnimatedVisibility(visible = expanded || pattern.group == NotePatternGroup.TRIPLET) {
            Row(
                modifier = rowModifier,
                horizontalArrangement = arrangement,
            ) {
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.TRIPLET_X1,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.half_triplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.TRIPLET_X2,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.quarter_triplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.TRIPLET_X4,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.eighth_triplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.TRIPLET_X8,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.sixteenth_triplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.TRIPLET_X16,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.thirty_second_triplet),
                )
            }
        }

        RowAnimatedVisibility(visible = expanded || pattern.group == NotePatternGroup.QUINTUPLET) {
            Row(
                modifier = rowModifier,
                horizontalArrangement = arrangement,
            ) {
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.QUINTUPLET_X1,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.quarter_quintuplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.QUINTUPLET_X2,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.eighth_quintuplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.QUINTUPLET_X4,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.sixteenth_quintuplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.QUINTUPLET_X8,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.thirty_second_quintuplet),
                )
            }
        }

        RowAnimatedVisibility(visible = expanded || pattern.group == NotePatternGroup.SEPTUPLET) {
            Row(
                modifier = rowModifier,
                horizontalArrangement = arrangement,
            ) {
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.SEPTUPLET_X1,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.quarter_septuplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.SEPTUPLET_X2,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.eighth_septuplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.SEPTUPLET_X4,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.sixteenth_septuplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.SEPTUPLET_X8,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.thirty_second_septuplet),
                )
            }
        }

        RowAnimatedVisibility(visible = expanded || pattern.group == NotePatternGroup.DISPLACED) {
            Row(
                modifier = rowModifier,
                horizontalArrangement = arrangement,
            ) {
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.DISPLACED_X1,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.displaced_half),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.DISPLACED_X2,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.displaced_quarter),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.DISPLACED_X4,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.displaced_eighth),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.DISPLACED_X8,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.displaced_sixteenth),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.DISPLACED_X16,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.displaced_thirty_second),
                )
            }
        }
    }
}

@Composable
private fun QuarterNoteLayout(
    pattern: NotePattern,
    onSubdivisionChoose: (NotePattern) -> Unit,
    modifier: Modifier,
    arrangement: Arrangement.HorizontalOrVertical,
    expanded: Boolean,
) {
    Column(
        modifier = modifier,
        verticalArrangement = arrangement,
    ) {
        val rowModifier = Modifier.fillMaxWidth()

        RowAnimatedVisibility(visible = expanded || pattern.group == NotePatternGroup.STRAIGHT) {
            Row(
                modifier = rowModifier,
                horizontalArrangement = arrangement,
            ) {
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.STRAIGHT_X1,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.quarter),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.STRAIGHT_X2,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.eighth),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.STRAIGHT_X4,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.sixteenth),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.STRAIGHT_X8,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.thirty_second),
                )
            }
        }

        RowAnimatedVisibility(visible = expanded || pattern.group == NotePatternGroup.TRIPLET) {
            Row(
                modifier = rowModifier,
                horizontalArrangement = arrangement,
            ) {
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.TRIPLET_X1,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.quarter_triplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.TRIPLET_X2,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.eighth_triplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.TRIPLET_X4,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.sixteenth_triplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.TRIPLET_X8,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.thirty_second_triplet),
                )
            }
        }

        RowAnimatedVisibility(visible = expanded || pattern.group == NotePatternGroup.QUINTUPLET) {
            Row(
                modifier = rowModifier,
                horizontalArrangement = arrangement,
            ) {
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.QUINTUPLET_X1,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.quarter_quintuplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.QUINTUPLET_X2,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.eighth_quintuplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.QUINTUPLET_X4,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.sixteenth_quintuplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.QUINTUPLET_X8,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.thirty_second_quintuplet),
                )
            }
        }

        RowAnimatedVisibility(visible = expanded || pattern.group == NotePatternGroup.SEPTUPLET) {
            Row(
                modifier = rowModifier,
                horizontalArrangement = arrangement,
            ) {
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.SEPTUPLET_X1,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.quarter_septuplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.SEPTUPLET_X2,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.eighth_septuplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.SEPTUPLET_X4,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.sixteenth_septuplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.SEPTUPLET_X8,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.thirty_second_septuplet),
                )
            }
        }

        RowAnimatedVisibility(visible = expanded || pattern.group == NotePatternGroup.DISPLACED) {
            Row(
                modifier = rowModifier,
                horizontalArrangement = arrangement,
            ) {
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.DISPLACED_X1,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.displaced_quarter),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.DISPLACED_X2,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.displaced_eighth),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.DISPLACED_X4,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.displaced_sixteenth),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.DISPLACED_X8,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.displaced_thirty_second),
                )
            }
        }
    }
}

@Composable
private fun EighthNoteLayout(
    pattern: NotePattern,
    onSubdivisionChoose: (NotePattern) -> Unit,
    modifier: Modifier,
    arrangement: Arrangement.HorizontalOrVertical,
    expanded: Boolean,
) {
    Column(
        modifier = modifier,
        verticalArrangement = arrangement,
    ) {
        val rowModifier = Modifier.fillMaxWidth()

        RowAnimatedVisibility(visible = expanded || pattern.group == NotePatternGroup.STRAIGHT) {
            Row(
                modifier = rowModifier,
                horizontalArrangement = arrangement,
            ) {
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.STRAIGHT_X1,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.eighth),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.STRAIGHT_X2,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.sixteenth),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.STRAIGHT_X4,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.thirty_second),
                )
            }
        }

        RowAnimatedVisibility(visible = expanded || pattern.group == NotePatternGroup.TRIPLET) {
            Row(
                modifier = rowModifier,
                horizontalArrangement = arrangement,
            ) {
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.TRIPLET_X1,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.eighth_triplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.TRIPLET_X2,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.sixteenth_triplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.TRIPLET_X4,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.thirty_second_triplet),
                )
            }
        }

        RowAnimatedVisibility(visible = expanded || pattern.group == NotePatternGroup.QUINTUPLET) {
            Row(
                modifier = rowModifier,
                horizontalArrangement = arrangement,
            ) {
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.QUINTUPLET_X1,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.eighth_quintuplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.QUINTUPLET_X2,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.sixteenth_quintuplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.QUINTUPLET_X4,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.thirty_second_quintuplet),
                )
            }
        }

        RowAnimatedVisibility(visible = expanded || pattern.group == NotePatternGroup.SEPTUPLET) {
            Row(
                modifier = rowModifier,
                horizontalArrangement = arrangement,
            ) {
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.SEPTUPLET_X1,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.eighth_septuplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.SEPTUPLET_X2,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.sixteenth_septuplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.SEPTUPLET_X4,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.thirty_second_septuplet),
                )
            }
        }

        RowAnimatedVisibility(visible = expanded || pattern.group == NotePatternGroup.DISPLACED) {
            Row(
                modifier = rowModifier,
                horizontalArrangement = arrangement,
            ) {
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.DISPLACED_X1,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.displaced_eighth),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.DISPLACED_X2,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.displaced_sixteenth),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.DISPLACED_X4,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.displaced_thirty_second),
                )
            }
        }
    }
}

@Composable
private fun SixteenthNoteLayout(
    pattern: NotePattern,
    onSubdivisionChoose: (NotePattern) -> Unit,
    modifier: Modifier,
    arrangement: Arrangement.HorizontalOrVertical,
    expanded: Boolean,
) {
    Column(
        modifier = modifier,
        verticalArrangement = arrangement,
    ) {
        val rowModifier = Modifier.fillMaxWidth()

        RowAnimatedVisibility(visible = expanded || pattern.group == NotePatternGroup.STRAIGHT) {
            Row(
                modifier = rowModifier,
                horizontalArrangement = arrangement,
            ) {
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.STRAIGHT_X1,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.sixteenth),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.STRAIGHT_X2,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.thirty_second),
                )
            }
        }

        RowAnimatedVisibility(visible = expanded || pattern.group == NotePatternGroup.TRIPLET) {
            Row(
                modifier = rowModifier,
                horizontalArrangement = arrangement,
            ) {
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.TRIPLET_X1,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.sixteenth_triplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.TRIPLET_X2,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.thirty_second_triplet),
                )
            }
        }

        RowAnimatedVisibility(visible = expanded || pattern.group == NotePatternGroup.QUINTUPLET) {
            Row(
                modifier = rowModifier,
                horizontalArrangement = arrangement,
            ) {
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.QUINTUPLET_X1,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.sixteenth_quintuplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.QUINTUPLET_X2,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.thirty_second_quintuplet),
                )
            }
        }

        RowAnimatedVisibility(visible = expanded || pattern.group == NotePatternGroup.SEPTUPLET) {
            Row(
                modifier = rowModifier,
                horizontalArrangement = arrangement,
            ) {
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.SEPTUPLET_X1,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.sixteenth_septuplet),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.SEPTUPLET_X2,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.thirty_second_septuplet),
                )
            }
        }

        AnimatedVisibility(visible = expanded || pattern.group == NotePatternGroup.DISPLACED) {
            Row(
                modifier = rowModifier,
                horizontalArrangement = arrangement,
            ) {
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.DISPLACED_X1,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.displaced_sixteenth),
                )
                SubdivisionItem(
                    actualPattern = pattern,
                    expectedPattern = NotePattern.DISPLACED_X2,
                    onSubdivisionChoose = onSubdivisionChoose,
                    iconPainter = painterResource(MR.images.displaced_thirty_second),
                )
            }
        }
    }
}

@Composable
private fun ThirtySecondNoteLayout(
    pattern: NotePattern,
    onSubdivisionChoose: (NotePattern) -> Unit,
    modifier: Modifier,
    arrangement: Arrangement.HorizontalOrVertical,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = arrangement,
    ) {
        SubdivisionItem(
            actualPattern = pattern,
            expectedPattern = NotePattern.STRAIGHT_X1,
            onSubdivisionChoose = onSubdivisionChoose,
            iconPainter = painterResource(MR.images.thirty_second),
        )
        SubdivisionItem(
            actualPattern = pattern,
            expectedPattern = NotePattern.TRIPLET_X1,
            onSubdivisionChoose = onSubdivisionChoose,
            iconPainter = painterResource(MR.images.thirty_second_triplet),
        )
        SubdivisionItem(
            actualPattern = pattern,
            expectedPattern = NotePattern.QUINTUPLET_X1,
            onSubdivisionChoose = onSubdivisionChoose,
            iconPainter = painterResource(MR.images.thirty_second_quintuplet),
        )
        SubdivisionItem(
            actualPattern = pattern,
            expectedPattern = NotePattern.SEPTUPLET_X1,
            onSubdivisionChoose = onSubdivisionChoose,
            iconPainter = painterResource(MR.images.thirty_second_septuplet),
        )
        SubdivisionItem(
            actualPattern = pattern,
            expectedPattern = NotePattern.DISPLACED_X1,
            onSubdivisionChoose = onSubdivisionChoose,
            iconPainter = painterResource(MR.images.displaced_thirty_second),
        )
    }
}

@Composable
private fun RowScope.SubdivisionItem(
    actualPattern: NotePattern,
    expectedPattern: NotePattern,
    onSubdivisionChoose: (NotePattern) -> Unit,
    iconPainter: Painter,
) {
    SubdivisionItem(
        onClick = { onSubdivisionChoose(expectedPattern) },
        isSelected = actualPattern == expectedPattern,
        iconPainter = iconPainter,
    )
}

@Composable
private fun RowScope.SubdivisionItem(onClick: () -> Unit, isSelected: Boolean, iconPainter: Painter) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .selectableBorder(isSelected)
            .clickable(onClick = onClick)
            .padding(8.dp),
    ) {
        Icon(
            painter = iconPainter,
            modifier = Modifier.align(Alignment.Center),
            contentDescription = null,
        )
    }
}

@Composable
private fun RowAnimatedVisibility(visible: Boolean, content: @Composable AnimatedVisibilityScope.() -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        content = content,
    )
}

@Preview
@Composable
private fun Preview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        for (noteDuration in arrayOf(1, 2, 4, 8, 16, 32)) {
            var pattern by remember { mutableStateOf(NotePattern.STRAIGHT_X1) }
            SubdivisionsChooser(
                pattern = pattern,
                timeSignature = TimeSignature(4, noteDuration),
                onSubdivisionChoose = { pattern = it },
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
