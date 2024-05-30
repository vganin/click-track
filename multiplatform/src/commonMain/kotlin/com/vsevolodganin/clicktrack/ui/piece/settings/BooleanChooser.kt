package com.vsevolodganin.clicktrack.ui.piece.settings

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.vsevolodganin.clicktrack.utils.compose.Preview

@Composable
fun BooleanChooser(
    title: String,
    value: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    SettingItem(
        title = title,
        description = description,
        modifier = modifier.clickable(
            interactionSource = interactionSource,
            indication = LocalIndication.current,
            onClick = { onCheckedChange(!value) },
        ),
    ) {
        Switch(
            checked = value,
            onCheckedChange = onCheckedChange,
            interactionSource = interactionSource,
        )
    }
}

@Preview
@Composable
private fun Preview() {
    BooleanChooser(
        title = "Very nice",
        value = false,
        onCheckedChange = {},
        modifier = Modifier,
    )
}
