package com.vsevolodganin.clicktrack.ui.piece.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import clicktrack.multiplatform.generated.resources.Res
import clicktrack.multiplatform.generated.resources.general_cancel
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

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
        modifier = modifier.clickable { showChooser = true },
    ) {
        Text(text = value)
    }

    if (showChooser) {
        AlertDialog(
            title = { Text(text = title) },
            confirmButton = {},
            onDismissRequest = { showChooser = false },
            dismissButton = {
                TextButton(onClick = { showChooser = false }) {
                    Text(text = stringResource(Res.string.general_cancel).uppercase())
                }
            },
            text = {
                DialogContent(
                    variants = variants,
                    onChoose = { value ->
                        showChooser = false
                        onChoose(value)
                    },
                )
            },
        )
    }
}

@Composable
private fun <T> DialogContent(variants: List<ListChooserItem<T>>, onChoose: (T) -> Unit) {
    Column {
        variants.forEachIndexed { index, item ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onChoose(item.value)
                    }
                    .padding(8.dp),
            ) {
                Text(text = item.displayValue)
                if (item.description != null) {
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            if (index != variants.lastIndex) {
                HorizontalDivider()
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
