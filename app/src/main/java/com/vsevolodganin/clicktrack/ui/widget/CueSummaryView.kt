package com.vsevolodganin.clicktrack.ui.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vsevolodganin.clicktrack.lib.Cue
import com.vsevolodganin.clicktrack.ui.preview.PREVIEW_CLICK_TRACK_1
import com.vsevolodganin.clicktrack.utils.compose.widthInByText

@Composable
fun CueSummaryView(cue: Cue) {
    val cueName = cue.name
    if (cueName == null || cueName.isBlank()) {
        BriefSummary(cue)
    } else {
        DetailedSummary(cueName, cue)
    }
}

@Composable
private fun BriefSummary(cue: Cue) {
    Row {
        Text(
            text = cue.bpm.value.toString(),
            modifier = Modifier.align(Alignment.CenterVertically),
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.width(2.dp))
        Column(Modifier.width(IntrinsicSize.Min)) {
            @Composable
            fun Text(text: String, modifier: Modifier) {
                Text(
                    text = text,
                    modifier = modifier,
                    fontSize = 9.sp
                )
            }

            val textModifier = Modifier.align(Alignment.CenterHorizontally)

            Text(text = cue.timeSignature.noteCount.toString(), textModifier)
            Text(text = cue.timeSignature.noteValue.toString(), textModifier)
        }
    }
}

@Composable
private fun DetailedSummary(title: String, cue: Cue) {
    Column {
        val textStyle = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.SemiBold)
        Text(
            text = title,
            modifier = Modifier.widthInByText(maxText = "MMMMMMMM", style = textStyle),
            style = textStyle,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        BriefSummary(cue)
    }
}

@Preview
@Composable
private fun Preview() {
    CueSummaryView(PREVIEW_CLICK_TRACK_1.value.cues[0])
}
