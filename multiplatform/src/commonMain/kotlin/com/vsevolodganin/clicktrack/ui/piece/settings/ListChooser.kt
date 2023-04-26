package com.vsevolodganin.clicktrack.ui.piece.settings

import ClickTrack.multiplatform.MR
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.utils.compose.AlertDialog
import com.vsevolodganin.clicktrack.utils.compose.Preview
import dev.icerock.moko.resources.compose.stringResource

data class ListChooserItem<T>(
    val value: T,
    val displayValue: String,
    val description: String?,
)

@Composable
fun <T> ListChooser(
    title: String,
    value: String,
    variants: List<ListChooserItem<T>>,
    onChoose: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showChooser by remember { mutableStateOf(false) }

    SettingItem(
        title = title,
        modifier = modifier.clickable { showChooser = true }
    ) {
        Text(text = value, modifier = Modifier.alpha(ContentAlpha.medium))
    }

    if (showChooser) {
        AlertDialog(
            title = { Text(text = title) },
            confirmButton = {},
            onDismissRequest = { showChooser = false },
            dismissButton = {
                TextButton(
                    onClick = { showChooser = false },
                    shape = RectangleShape
                ) {
                    Text(text = stringResource(MR.strings.general_cancel).uppercase())
                }
            },
            text = {
                DialogContent(
                    variants = variants,
                    onChoose = { value ->
                        showChooser = false
                        onChoose(value)
                    }
                )
            },
        )
    }
}

@Composable
private fun <T> DialogContent(
    variants: List<ListChooserItem<T>>,
    onChoose: (T) -> Unit,
) {
    Column {
        variants.forEachIndexed { index, item ->
            Column(modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onChoose(item.value)
                }
                .padding(8.dp)
            ) {
                Text(text = item.displayValue)
                if (item.description != null) {
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.caption,
                    )
                }
            }

            if (index != variants.lastIndex) {
                Divider()
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ListChooser(
        title = "Number option",
        value = PreviewData[1].displayValue,
        variants = PreviewData,
        onChoose = {},
        modifier = Modifier,
    )
}

@Preview
@Composable
private fun DialogPreview() {
    DialogContent(
        variants = PreviewData,
        onChoose = {},
    )
}

private val PreviewData = listOf(
    ListChooserItem(
        value = 0,
        displayValue = "Zero",
        description = "Just regular zero",
    ),
    ListChooserItem(
        value = 1,
        displayValue = "One",
        description = "More sophisticated number - a one",
    ),
    ListChooserItem(
        value = 2,
        displayValue = "Two",
        description = "Yep, that's a fucking two",
    ),
)
