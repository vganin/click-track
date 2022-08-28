package com.vsevolodganin.clicktrack.ui.piece

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun <T> DropdownSelector(
    items: List<T>,
    selectedValue: T,
    onSelect: (T) -> Unit,
    toString: @Composable (T) -> String,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    DisposableEffect(expanded) {
        if (expanded) {
            focusRequester.requestFocus()
        } else {
            focusManager.clearFocus()
        }
        onDispose {}
    }

    Column(modifier = modifier.width(IntrinsicSize.Min)) {
        Row(
            modifier = Modifier
                .focusRequester(focusRequester)
                .focusableBorder()
                .focusable()
                .clickable { expanded = !expanded }
                .padding(8.dp)
        ) {
            Text(
                text = toString(selectedValue),
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                softWrap = false,
            )
            Spacer(modifier = Modifier.width(8.dp))
            ExpandableChevron(isExpanded = expanded)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            content = {
                items.forEach { value ->
                    DropdownMenuItem(onClick = {
                        onSelect(value)
                        expanded = false
                    }) {
                        Text(toString(value))
                    }
                }
            },
        )
    }
}

@Preview
@Composable
private fun Preview() {
    val items = remember {
        listOf(
            "One",
            "Two",
            "Three",
            "∞",
        )
    }
    var selectedValue by remember { mutableStateOf(items[0]) }

    DropdownSelector(
        items = items,
        selectedValue = selectedValue,
        onSelect = { value -> selectedValue = value },
        toString = { it },
        modifier = Modifier
    )
}
