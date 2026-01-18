package com.vsevolodganin.clicktrack.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.CenterFocusWeak
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Card
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import clicktrack.multiplatform.generated.resources.Res
import clicktrack.multiplatform.generated.resources.sound_library_screen_title
import com.vsevolodganin.clicktrack.model.BuiltinClickSounds
import com.vsevolodganin.clicktrack.model.ClickSoundType
import com.vsevolodganin.clicktrack.model.ClickSoundsId
import com.vsevolodganin.clicktrack.soundlibrary.SelectableClickSoundsItem
import com.vsevolodganin.clicktrack.soundlibrary.SoundLibraryState
import com.vsevolodganin.clicktrack.soundlibrary.SoundLibraryViewModel
import com.vsevolodganin.clicktrack.ui.piece.DarkTopAppBarWithBack
import com.vsevolodganin.clicktrack.ui.piece.PlayStopIcon
import com.vsevolodganin.clicktrack.ui.piece.selectableBorder
import com.vsevolodganin.clicktrack.ui.theme.ClickTrackTheme
import com.vsevolodganin.clicktrack.utils.compose.SwipeToDelete
import com.vsevolodganin.clicktrack.utils.compose.withFabPadding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.stringResource

@Composable
fun SoundLibraryScreenView(viewModel: SoundLibraryViewModel, modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            DarkTopAppBarWithBack(
                onBackClick = viewModel::onBackClick,
                title = { Text(stringResource(Res.string.sound_library_screen_title)) },
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::onAddNewClick,
                shape = CircleShape,
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        },
        modifier = modifier,
    ) { paddingValues ->
        val state by viewModel.state.collectAsState()
        Content(
            viewModel = viewModel,
            state = state ?: return@Scaffold,
            paddingValues = paddingValues,
        )
    }
}

@Composable
private fun Content(
    viewModel: SoundLibraryViewModel,
    state: SoundLibraryState,
    paddingValues: PaddingValues,
) {
    LazyColumn(
        contentPadding = paddingValues.withFabPadding(),
    ) {
        items(items = state.items, key = { Json.encodeToString(it.id) }) { item ->
            ClicksSoundsItem(viewModel, item)
        }
    }
}

@Composable
private fun ClicksSoundsItem(viewModel: SoundLibraryViewModel, item: SelectableClickSoundsItem) {
    when (item) {
        is SelectableClickSoundsItem.Builtin -> BuiltinClickSoundsItem(viewModel, item)
        is SelectableClickSoundsItem.UserDefined -> UserDefinedSoundsItem(viewModel, item)
    }
}

@Composable
private fun BuiltinClickSoundsItem(viewModel: SoundLibraryViewModel, item: SelectableClickSoundsItem.Builtin) {
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
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun UserDefinedSoundsItem(viewModel: SoundLibraryViewModel, item: SelectableClickSoundsItem.UserDefined) {
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
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Row {
                        Icon(
                            imageVector = Icons.Default.CenterFocusStrong,
                            contentDescription = null,
                            modifier = Modifier.align(CenterVertically),
                        )

                        Spacer(modifier = Modifier.size(16.dp))

                        FileField(
                            text = item.strongBeatValue,
                            onClick = { viewModel.onItemSoundSelect(item.id, ClickSoundType.STRONG) },
                        )
                    }

                    Row {
                        Icon(
                            imageVector = Icons.Default.CenterFocusWeak,
                            contentDescription = null,
                            modifier = Modifier.align(CenterVertically),
                        )

                        Spacer(modifier = Modifier.size(16.dp))

                        FileField(
                            text = item.weakBeatValue,
                            onClick = { viewModel.onItemSoundSelect(item.id, ClickSoundType.WEAK) },
                        )
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
                            tint = MaterialTheme.colorScheme.error,
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

@Composable
private fun FileField(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .selectableBorder(isSelected = false)
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
    }
}

@Preview
@Composable
internal fun SoundLibraryScreenPreview() = ClickTrackTheme {
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

            override fun onItemSoundSelect(id: ClickSoundsId.Database, type: ClickSoundType) = Unit

            override fun onItemSoundTestToggle(id: ClickSoundsId.Database) = Unit
        },
    )
}
