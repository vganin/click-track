package com.vsevolodganin.clicktrack.view.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vsevolodganin.clicktrack.lib.Cue
import com.vsevolodganin.clicktrack.view.preview.PREVIEW_CLICK_TRACK_1

@Composable
fun CueSummary(
    cue: Cue,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
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

@Preview
@Composable
fun PreviewCueSummary() {
    CueSummary(PREVIEW_CLICK_TRACK_1.value.cues[0])
}
