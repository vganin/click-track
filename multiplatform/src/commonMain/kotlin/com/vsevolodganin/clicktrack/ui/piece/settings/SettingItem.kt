package com.vsevolodganin.clicktrack.ui.piece.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingItem(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    value: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .defaultMinSize(minHeight = 56.dp)
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f),
        ) {
            Text(text = title)

            if (description != null) {
                Spacer(modifier = Modifier.height(4.dp))
                CompositionLocalProvider(
                    LocalContentAlpha provides ContentAlpha.medium,
                ) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.caption,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Box(modifier = Modifier.align(Alignment.CenterVertically)) {
            value()
        }
    }
}
