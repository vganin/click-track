package com.vsevolodganin.clicktrack.view.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.AmbientContentColor
import androidx.compose.material.Divider
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
            text = "${cue.bpm.value},",
            modifier = Modifier.align(Alignment.CenterVertically),
        )
        Spacer(modifier = Modifier.width(2.dp))
        Column(Modifier.preferredWidth(IntrinsicSize.Min)) {
            @Composable
            fun Text(text: String) {
                Text(
                    text = text,
                    lineHeight = 2.sp,
                    fontSize = 10.sp
                )
            }

            Text(text = cue.timeSignature.noteDuration.toString())
            Divider(color = AmbientContentColor.current, modifier = Modifier.fillMaxWidth().height(1.dp))
            Text(text = cue.timeSignature.noteCount.toString())
        }
    }
}

@Preview
@Composable
fun PreviewCueSummary() {
    CueSummary(PREVIEW_CLICK_TRACK_1.value.cues[0].cue)
}
