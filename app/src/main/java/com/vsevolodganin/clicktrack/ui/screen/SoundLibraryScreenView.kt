package com.vsevolodganin.clicktrack.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.runtime.Composable
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
import com.vsevolodganin.clicktrack.utils.compose.padWithFabSpace
import com.vsevolodganin.clicktrack.utils.compose.swipeToRemove

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
}

@Composable
private fun Content(
    state: SoundLibraryUiState,
    dispatch: Dispatch,
) {
    val enterTransition = remember { slideInHorizontally(initialOffsetX = { it / 2 }) }
    val exitTransition = remember { slideOutHorizontally(targetOffsetX = { it / 2 }) }

    LazyColumn {
        items(items = state.items, key = SelectableClickSoundsItem::id) { item ->
            AnimatedVisibility(
                visibleState = remember { MutableTransitionState(initialState = false).apply { targetState = true } },
                enter = enterTransition,
                exit = exitTransition,
            ) {
                ClicksSoundsItem(item, dispatch)
            }
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
    BoxWithConstraints {
        Card(
            modifier = Modifier
                .swipeToRemove(constraints = constraints) {
                    dispatch(SoundLibraryAction.RemoveClickSounds(item.id))
                }
                .fillMaxWidth()
                .padding(8.dp)
                .clickable(onClick = onSelect),
            elevation = 2.dp
        ) {
            ConstraintLayout(modifier = Modifier.padding(8.dp)) {
                val (selected, strongBeatIcon, weakBeatIcon, strongBeatValue, weakBeatValue, playStrongBeat, playWeakBeat) = createRefs()

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
                            end.linkTo(playStrongBeat.start)
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
                            end.linkTo(playWeakBeat.start)
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

                PlayButtonOrError(
                    modifier = Modifier.constrainAs(playStrongBeat) {
                        start.linkTo(strongBeatValue.end, 16.dp)
                        end.linkTo(parent.end)
                        top.linkTo(strongBeatIcon.top)
                        bottom.linkTo(strongBeatIcon.bottom)
                    },
                    hasError = item.strongBeatHasError,
                    onClick = { dispatch(SoundLibraryAction.PlaySound(item.id, ClickSoundType.STRONG)) },
                )

                PlayButtonOrError(
                    modifier = Modifier.constrainAs(playWeakBeat) {
                        start.linkTo(weakBeatValue.end, 16.dp)
                        end.linkTo(parent.end)
                        top.linkTo(weakBeatIcon.top)
                        bottom.linkTo(weakBeatIcon.bottom)
                    },
                    hasError = item.weakBeatHasError,
                    onClick = { dispatch(SoundLibraryAction.PlaySound(item.id, ClickSoundType.WEAK)) }
                )
            }
        }
    }
}

@Composable
private fun PlayButtonOrError(modifier: Modifier, hasError: Boolean, onClick: () -> Unit) {
    if (hasError) {
        IconButton(
            modifier = modifier,
            enabled = false,
            onClick = {}
        ) {
            Icon(imageVector = Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colors.error)
        }
    } else {
        IconButton(
            modifier = modifier,
            onClick = onClick,
        ) {
            Icon(imageVector = Icons.Default.PlayCircle, contentDescription = null)
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
                    strongBeatHasError = false,
                    weakBeatValue = "/Downloads/no_access.mp3",
                    weakBeatHasError = true,
                    selected = false
                )
            ),
        ),
    )
}