package com.vsevolodganin.clicktrack.ui.piece

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vsevolodganin.clicktrack.model.Cue
import com.vsevolodganin.clicktrack.model.CueDuration
import com.vsevolodganin.clicktrack.model.TimeSignature
import com.vsevolodganin.clicktrack.model.bpm
import com.vsevolodganin.clicktrack.utils.compose.Preview
import com.vsevolodganin.clicktrack.utils.compose.widthInByText

@Composable
fun CueSummaryView(cue: Cue) {
    val cueName = cue.name
    if (cueName.isNullOrBlank()) {
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
        Text(
            text = "${cue.timeSignature.noteCount}\n${cue.timeSignature.noteValue}",
            textAlign = TextAlign.Center,
            style = LocalTextStyle.current.copy(fontSize = 9.sp),
            lineHeight = 7.sp
        )
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
    CueSummaryView(
        Cue(
            bpm = 60.bpm,
            timeSignature = TimeSignature(3, 4),
            duration = CueDuration.Beats(4),
        )
    )
}
