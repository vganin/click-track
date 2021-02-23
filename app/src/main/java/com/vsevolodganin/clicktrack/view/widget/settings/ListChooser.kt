package com.vsevolodganin.clicktrack.view.widget.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.utils.compose.toUpperCase

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

    Row(modifier = modifier
        .clickable { showChooser = true }
        .padding(16.dp)
    ) {
        Text(text = title, modifier = Modifier.weight(1f))
        Text(text = value, style = MaterialTheme.typography.caption)
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
                    Text(text = stringResource(id = android.R.string.cancel).toUpperCase())
                }
            },
            text = {
                Column {
                    variants.forEach { item ->
                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showChooser = false
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
                    }
                }
            }
        )
    }
}
