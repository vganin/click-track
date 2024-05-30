package com.vsevolodganin.clicktrack.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ButtonDefaults
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.generated.resources.MR
import com.vsevolodganin.clicktrack.model.BuiltinClickSounds
import com.vsevolodganin.clicktrack.model.ClickSoundType
import com.vsevolodganin.clicktrack.model.ClickSoundsId
import com.vsevolodganin.clicktrack.soundlibrary.SelectableClickSoundsItem
import com.vsevolodganin.clicktrack.soundlibrary.SoundLibraryState
import com.vsevolodganin.clicktrack.soundlibrary.SoundLibraryViewModel
import com.vsevolodganin.clicktrack.ui.piece.FloatingActionButton
import com.vsevolodganin.clicktrack.ui.piece.PlayStopIcon
import com.vsevolodganin.clicktrack.ui.piece.TopAppBarWithBack
import com.vsevolodganin.clicktrack.ui.theme.ClickTrackTheme
import com.vsevolodganin.clicktrack.ui.theme.CommonCardElevation
import com.vsevolodganin.clicktrack.utils.compose.SwipeToDelete
import com.vsevolodganin.clicktrack.utils.compose.padWithFabSpace
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun SoundLibraryScreenView(
    viewModel: SoundLibraryViewModel,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBarWithBack(
                onBackClick = viewModel::onBackClick,
                title = { Text(stringResource(MR.strings.sound_library_screen_title)) },
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::onAddNewClick) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        },
        modifier = modifier,
    ) {
        val state by viewModel.state.collectAsState()
        Content(viewModel, state ?: return@Scaffold)
    }
}

@Composable
private fun Content(
    viewModel: SoundLibraryViewModel,
    state: SoundLibraryState,
) {
    LazyColumn {
        items(items = state.items, key = SelectableClickSoundsItem::id) { item ->
            ClicksSoundsItem(viewModel, item)
        }

        padWithFabSpace()
    }
}

@Composable
private fun ClicksSoundsItem(
    viewModel: SoundLibraryViewModel,
    item: SelectableClickSoundsItem,
) {
    when (item) {
        is SelectableClickSoundsItem.Builtin -> BuiltinClickSoundsItem(viewModel, item)
        is SelectableClickSoundsItem.UserDefined -> UserDefinedSoundsItem(viewModel, item)
    }
}

@Composable
private fun BuiltinClickSoundsItem(
    viewModel: SoundLibraryViewModel,
    item: SelectableClickSoundsItem.Builtin,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { viewModel.onItemClick(item.id) })
            .padding(start = 8.dp),
    ) {
        RadioButton(
            selected = item.selected,
            modifier = Modifier.align(CenterVertically),
            onClick = { viewModel.onItemClick(item.id) },
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = stringResource(item.data.nameResource),
            modifier = Modifier
                .fillMaxWidth()
                .align(CenterVertically),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.subtitle1,
        )
    }
}

@Composable
private fun UserDefinedSoundsItem(
    viewModel: SoundLibraryViewModel,
    item: SelectableClickSoundsItem.UserDefined,
) {
    val contentPadding = 8.dp

    SwipeToDelete(
        onDeleted = { viewModel.onItemRemove(item.id) },
        contentPadding = contentPadding,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding)
                .clickable(onClick = { viewModel.onItemClick(item.id) }),
            elevation = CommonCardElevation.Normal,
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                RadioButton(
                    selected = item.selected,
                    onClick = { viewModel.onItemClick(item.id) },
                    Modifier.align(CenterVertically),
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Row {
                        Icon(
                            imageVector = Icons.Default.CenterFocusStrong,
                            contentDescription = null,
                            modifier = Modifier.align(CenterVertically),
                        )

                        Spacer(modifier = Modifier.size(16.dp))

                        OutlinedButton(
                            onClick = { viewModel.onItemSoundSelect(item.id, ClickSoundType.STRONG) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.secondary),
                        ) {
                            Text(
                                text = item.strongBeatValue,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                            )
                        }
                    }

                    Row {
                        Icon(
                            imageVector = Icons.Default.CenterFocusWeak,
                            contentDescription = null,
                            modifier = Modifier.align(CenterVertically),
                        )

                        Spacer(modifier = Modifier.size(16.dp))

                        OutlinedButton(
                            onClick = { viewModel.onItemSoundSelect(item.id, ClickSoundType.WEAK) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.secondary),
                        ) {
                            Text(
                                text = item.weakBeatValue,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                            )
                        }
                    }
                }

                if (item.hasError) {
                    IconButton(
                        enabled = false,
                        onClick = {},
                        modifier = Modifier.align(CenterVertically),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colors.error,
                        )
                    }
                } else {
                    IconButton(
                        onClick = { viewModel.onItemSoundTestToggle(item.id) },
                        modifier = Modifier.align(CenterVertically),
                    ) {
                        PlayStopIcon(isPlaying = item.isPlaying)
                    }
                }
            }
        }
    }
}

@ScreenPreview
@Composable
private fun Preview() =
    ClickTrackTheme {
        SoundLibraryScreenView(
            viewModel = object : SoundLibraryViewModel {
                override val state: StateFlow<SoundLibraryState?> = MutableStateFlow(
                    SoundLibraryState(
                        items = listOf(
                            SelectableClickSoundsItem.Builtin(
                                data = BuiltinClickSounds.BEEP,
                                selected = true,
                            ),
                            SelectableClickSoundsItem.UserDefined(
                                id = ClickSoundsId.Database(0L),
                                strongBeatValue = "/audio/audio/audio/audio/strong.mp3",
                                weakBeatValue = "/audio/audio/audio/audio/weak.mp3",
                                hasError = false,
                                isPlaying = true,
                                selected = false,
                            ),
                            SelectableClickSoundsItem.UserDefined(
                                id = ClickSoundsId.Database(1L),
                                strongBeatValue = "/audio/audio/audio/audio/strong.mp3",
                                weakBeatValue = "no_access.mp3",
                                hasError = true,
                                isPlaying = false,
                                selected = false,
                            ),
                        ),
                    ),
                )

                override fun onBackClick() = Unit

                override fun onAddNewClick() = Unit

                override fun onItemClick(id: ClickSoundsId) = Unit

                override fun onItemRemove(id: ClickSoundsId.Database) = Unit

                override fun onItemSoundSelect(
                    id: ClickSoundsId.Database,
                    type: ClickSoundType,
                ) = Unit

                override fun onItemSoundTestToggle(id: ClickSoundsId.Database) = Unit
            },
        )
    }
