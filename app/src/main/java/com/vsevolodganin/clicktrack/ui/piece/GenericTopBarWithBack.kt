package com.vsevolodganin.clicktrack.ui.piece

import androidx.annotation.StringRes
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.vsevolodganin.clicktrack.redux.action.BackAction
import com.vsevolodganin.clicktrack.redux.core.Dispatch

@Composable
fun GenericTopBarWithBack(@StringRes stringRes: Int, dispatch: Dispatch) {
    TopAppBar(
        title = { Text(text = stringResource(stringRes)) },
        navigationIcon = {
            IconButton(onClick = { dispatch(BackAction) }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        },
    )
}
