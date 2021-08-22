package com.vsevolodganin.clicktrack.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.CenterFocusWeak
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.vsevolodganin.clicktrack.R
import com.vsevolodganin.clicktrack.sounds.model.BuiltinClickSounds
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundType
import com.vsevolodganin.clicktrack.sounds.model.ClickSoundsId
import com.vsevolodganin.clicktrack.state.redux.action.SoundLibraryAction
import com.vsevolodganin.clicktrack.state.redux.core.Dispatch
import com.vsevolodganin.clicktrack.ui.model.SelectableClickSoundsItem
import com.vsevolodganin.clicktrack.ui.model.SoundLibraryUiState
import com.vsevolodganin.clicktrack.ui.widget.ClickTrackFloatingActionButton
import com.vsevolodganin.clicktrack.ui.widget.GenericTopBarWithBack
import com.vsevolodganin.clicktrack.ui.widget.PlayStopIcon
import com.vsevolodganin.clicktrack.utils.compose.SwipeToDismiss
import com.vsevolodganin.clicktrack.utils.compose.padWithFabSpace

@Composable
fun SoundLibraryScreenView(
    state: SoundLibraryUiState,
    modifier: Modifier = Modifier,
    dispatch: Dispatch = Dispatch {},
) {
    Scaffold(
        topBar = { GenericTopBarWithBack(R.string.sound_library, dispatch) },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            ClickTrackFloatingActionButton(onClick = { dispatch(SoundLibraryAction.AddNewClickSounds) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        },
        modifier = modifier,
    ) {
        Content(state, dispatch)
    }

    DisposableEffect(Unit) {
        onDispose {
            dispatch(SoundLibraryAction.StopSoundsTest)
        }
    }
}

@Composable
private fun Content(
    state: SoundLibraryUiState,
    dispatch: Dispatch,
) {
    LazyColumn {
        items(items = state.items, key = SelectableClickSoundsItem::id) { item ->
            ClicksSoundsItem(item, dispatch)
        }

        padWithFabSpace()
    }
}

@Composable
private fun ClicksSoundsItem(
    item: SelectableClickSoundsItem,
    dispatch: Dispatch,
) {
    val onSelect = remember {
        { dispatch(SoundLibraryAction.SelectClickSounds(item.id)) }
    }

    when (item) {
        is SelectableClickSoundsItem.Builtin -> BuiltinClickSoundsItem(item, onSelect)
        is SelectableClickSoundsItem.UserDefined -> UserDefinedSoundsItem(item, onSelect, dispatch)
    }
}

@Composable
private fun BuiltinClickSoundsItem(
    item: SelectableClickSoundsItem.Builtin,
    onSelect: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(16.dp)
    ) {
        RadioButton(
            selected = item.selected,
            modifier = Modifier.align(Alignment.CenterVertically),
            onClick = onSelect,
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = stringResource(item.data.nameStringRes),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.subtitle1
        )
    }
}

@Composable
private fun UserDefinedSoundsItem(
    item: SelectableClickSoundsItem.UserDefined,
    onSelect: () -> Unit,
    dispatch: Dispatch,
) {
    SwipeToDismiss(
        onDismiss = { dispatch(SoundLibraryAction.RemoveClickSounds(item.id)) },
        backgroundPaddingValues = PaddingValues(vertical = 8.dp),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable(onClick = onSelect),
            elevation = 2.dp
        ) {
            ConstraintLayout(modifier = Modifier.padding(8.dp)) {
                val (selected, strongBeatIcon, weakBeatIcon, strongBeatValue, weakBeatValue, playStopError) = createRefs()
                val valuesEnd = createEndBarrier(weakBeatValue, strongBeatValue)

                // FIXME(https://issuetracker.google.com/issues/181717954): Use proper barrier

                RadioButton(
                    selected = item.selected,
                    modifier = Modifier.constrainAs(selected) {
                        start.linkTo(parent.start)
                        centerVerticallyTo(parent)
                    },
                    onClick = onSelect,
                )

                Icon(
                    imageVector = Icons.Default.CenterFocusStrong,
                    contentDescription = null,
                    modifier = Modifier.constrainAs(strongBeatIcon) {
                        start.linkTo(selected.end, margin = 16.dp)
                        top.linkTo(strongBeatValue.top)
                        bottom.linkTo(strongBeatValue.bottom)
                    },
                )

                Icon(
                    imageVector = Icons.Default.CenterFocusWeak,
                    contentDescription = null,
                    modifier = Modifier.constrainAs(weakBeatIcon) {
                        start.linkTo(selected.end, margin = 16.dp)
                        top.linkTo(weakBeatValue.top)
                        bottom.linkTo(weakBeatValue.bottom)
                    },
                )

                OutlinedButton(
                    modifier = Modifier
                        .constrainAs(strongBeatValue) {
                            start.linkTo(strongBeatIcon.end, 16.dp)
                            top.linkTo(parent.top)
                            bottom.linkTo(weakBeatValue.top)
                            end.linkTo(valuesEnd)
                            width = Dimension.fillToConstraints
                        },
                    onClick = {
                        dispatch(SoundLibraryAction.SelectClickSound(item.id, ClickSoundType.STRONG))
                    },
                ) {
                    Text(
                        text = item.strongBeatValue,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                }

                OutlinedButton(
                    modifier = Modifier
                        .constrainAs(weakBeatValue) {
                            start.linkTo(strongBeatIcon.end, 16.dp)
                            top.linkTo(strongBeatValue.bottom, 8.dp)
                            bottom.linkTo(parent.bottom)
                            end.linkTo(valuesEnd)
                            width = Dimension.fillToConstraints
                        },
                    onClick = {
                        dispatch(SoundLibraryAction.SelectClickSound(item.id, ClickSoundType.WEAK))
                    },
                ) {
                    Text(
                        text = item.weakBeatValue,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                }

                val playStopErrorButtonModifier = Modifier.constrainAs(playStopError) {
                    start.linkTo(valuesEnd, 8.dp)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                if (item.hasError) {
                    IconButton(
                        enabled = false,
                        onClick = {},
                        modifier = playStopErrorButtonModifier
                    ) {
                        Icon(imageVector = Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colors.error)
                    }
                } else {
                    IconButton(
                        onClick = {
                            if (item.isPlaying) {
                                dispatch(SoundLibraryAction.StopSoundsTest)
                            } else {
                                dispatch(SoundLibraryAction.StartSoundsTest(item.id))
                            }
                        },
                        modifier = playStopErrorButtonModifier
                    ) {
                        PlayStopIcon(isPlaying = item.isPlaying)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    SoundLibraryScreenView(
        state = SoundLibraryUiState(
            items = listOf(
                SelectableClickSoundsItem.Builtin(
                    data = BuiltinClickSounds.BEEP,
                    selected = true
                ),
                SelectableClickSoundsItem.UserDefined(
                    id = ClickSoundsId.Database(0L),
                    strongBeatValue = "/audio/audio/audio/audio/strong.mp3",
                    weakBeatValue = "/audio/audio/audio/audio/weak.mp3",
                    hasError = false,
                    isPlaying = true,
                    selected = false
                ),
                SelectableClickSoundsItem.UserDefined(
                    id = ClickSoundsId.Database(1L),
                    strongBeatValue = "/audio/audio/audio/audio/strong.mp3",
                    weakBeatValue = "/Downloads/no_access.mp3",
                    hasError = true,
                    isPlaying = false,
                    selected = false
                )
            ),
        ),
    )
}
