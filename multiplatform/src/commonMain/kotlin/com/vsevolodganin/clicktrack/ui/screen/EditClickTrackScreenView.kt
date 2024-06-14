package com.vsevolodganin.clicktrack.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vsevolodganin.clicktrack.edit.EditClickTrackState
import com.vsevolodganin.clicktrack.edit.EditClickTrackViewModel
import com.vsevolodganin.clicktrack.edit.EditCueState
import com.vsevolodganin.clicktrack.edit.toEditState
import com.vsevolodganin.clicktrack.generated.resources.MR
import com.vsevolodganin.clicktrack.model.BeatsPerMinuteOffset
import com.vsevolodganin.clicktrack.model.CueDuration
import com.vsevolodganin.clicktrack.model.NotePattern
import com.vsevolodganin.clicktrack.model.TimeSignature
import com.vsevolodganin.clicktrack.ui.piece.BpmInputField
import com.vsevolodganin.clicktrack.ui.piece.Checkbox
import com.vsevolodganin.clicktrack.ui.piece.CueView
import com.vsevolodganin.clicktrack.ui.piece.ExpandableChevron
import com.vsevolodganin.clicktrack.ui.piece.FloatingActionButton
import com.vsevolodganin.clicktrack.ui.piece.TopAppBarWithBack
import com.vsevolodganin.clicktrack.ui.preview.PREVIEW_CLICK_TRACK_1
import com.vsevolodganin.clicktrack.ui.theme.ClickTrackTheme
import com.vsevolodganin.clicktrack.ui.theme.CommonCardElevation
import com.vsevolodganin.clicktrack.ui.theme.commonCardElevation
import com.vsevolodganin.clicktrack.utils.compose.SwipeToDelete
import com.vsevolodganin.clicktrack.utils.compose.padWithFabSpace
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.ReorderableItemScope
import sh.calvin.reorderable.rememberReorderableLazyColumnState
import kotlin.math.roundToInt

@Composable
fun EditClickTrackScreenView(viewModel: EditClickTrackViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBarWithBack(
                onBackClick = viewModel::onBackClick,
                title = { Text(stringResource(MR.strings.edit_click_track_screen_title)) },
                actions = {
                    if (state?.showForwardButton == true) {
                        IconButton(onClick = viewModel::onForwardClick) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null)
                        }
                    }
                },
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.onAddNewCueClick()
                    coroutineScope.launch {
                        listState.scrollToItem(listState.layoutInfo.totalItemsCount - 1)
                    }
                },
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        },
        modifier = modifier,
    ) {
        Content(
            viewModel = viewModel,
            state = state ?: return@Scaffold,
            listState = listState,
        )
    }
}

@Composable
private fun Content(viewModel: EditClickTrackViewModel, state: EditClickTrackState, listState: LazyListState) {
    // This is a number of non draggable items before cues to offset indices correctly
    val numberOfNonDraggableItems = 3

    val reorderableLazyColumnState = rememberReorderableLazyColumnState(listState) { from, to ->
        viewModel.onItemMove(from.index - numberOfNonDraggableItems, to.index - numberOfNonDraggableItems)
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
    ) {
        stickyHeader {
            Surface(
                elevation = CommonCardElevation.Normal,
            ) {
                TextField(
                    value = state.name,
                    onValueChange = viewModel::onNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(text = stringResource(MR.strings.edit_click_track_title_hint)) },
                    textStyle = MaterialTheme.typography.h6,
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.surface),
                )
            }
        }

        item {
            Spacer(modifier = Modifier.size(8.dp))
        }

        item {
            OptionsItem(
                viewModel = viewModel,
                loop = state.loop,
                tempoOffset = state.tempoOffset,
            )
        }

        itemsIndexed(items = state.cues, key = { _, cue -> cue.id }) { index, cue ->
            ReorderableItem(reorderableLazyListState = reorderableLazyColumnState, key = cue.id) { isDragging ->
                CueListItem(
                    viewModel = viewModel,
                    cue = cue,
                    index = index,
                    elevation = commonCardElevation(isDragging),
                )
            }
        }

        padWithFabSpace()
    }
}

@Composable
private fun OptionsItem(viewModel: EditClickTrackViewModel, loop: Boolean, tempoOffset: BeatsPerMinuteOffset) {
    var optionsExpanded by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CommonCardElevation.Normal,
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = CenterVertically,
            ) {
                Text(
                    text = stringResource(MR.strings.edit_click_track_options),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.h6,
                )

                IconButton(
                    onClick = { optionsExpanded = !optionsExpanded },
                ) {
                    ExpandableChevron(isExpanded = optionsExpanded)
                }
            }

            AnimatedVisibility(
                visible = optionsExpanded,
                enter = fadeIn() + expandVertically(expandFrom = Bottom),
                exit = fadeOut() + shrinkVertically(shrinkTowards = Bottom),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    LoopItem(viewModel, loop)
                    TempoOffsetItem(viewModel, tempoOffset)
                }
            }
        }
    }
}

@Composable
private fun LoopItem(viewModel: EditClickTrackViewModel, loop: Boolean) {
    Row {
        Text(
            text = stringResource(MR.strings.edit_click_track_loop),
            modifier = Modifier
                .align(CenterVertically)
                .weight(1f),
        )

        Checkbox(
            checked = loop,
            onCheckedChange = viewModel::onLoopChange,
        )
    }
}

@Composable
private fun TempoOffsetItem(viewModel: EditClickTrackViewModel, tempoOffset: BeatsPerMinuteOffset) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = CenterVertically,
    ) {
        var floatTempoOffset by remember(tempoOffset) {
            mutableStateOf(tempoOffset.value.toFloat())
        }

        Slider(
            value = floatTempoOffset,
            onValueChange = { floatTempoOffset = it },
            modifier = Modifier.weight(1f),
            valueRange = TEMPO_OFFSET_RANGE.first.toFloat()..TEMPO_OFFSET_RANGE.last.toFloat(),
            steps = TEMPO_OFFSET_RANGE.count() - 2,
            onValueChangeFinished = { viewModel.onTempoOffsetChange(floatTempoOffset.roundToInt()) },
            colors = SliderDefaults.colors(
                activeTrackColor = MaterialTheme.colors.primary,
                inactiveTrackColor = MaterialTheme.colors.primary,
                activeTickColor = contentColorFor(MaterialTheme.colors.primary).copy(alpha = SliderDefaults.TickAlpha),
                inactiveTickColor = contentColorFor(MaterialTheme.colors.primary).copy(alpha = SliderDefaults.TickAlpha),
            ),
        )

        Spacer(modifier = Modifier.width(8.dp))

        BpmInputField(
            value = floatTempoOffset.roundToInt(),
            onValueChange = viewModel::onTempoOffsetChange,
            showSign = true,
            allowedNumbersRange = -999..999,
            fallbackNumber = 0,
        )
    }
}

@Composable
private fun ReorderableItemScope.CueListItem(
    viewModel: EditClickTrackViewModel,
    cue: EditCueState,
    index: Int,
    elevation: Dp,
    modifier: Modifier = Modifier,
) {
    SwipeToDelete(
        onDeleted = { viewModel.onCueRemove(index) },
        modifier = modifier,
        contentPadding = 8.dp,
    ) {
        Card(
            modifier = Modifier.padding(8.dp),
            elevation = elevation,
        ) {
            CueView(
                value = cue,
                onNameChange = { viewModel.onCueNameChange(index, it) },
                onBpmChange = { viewModel.onCueBpmChange(index, it) },
                onTimeSignatureChange = { viewModel.onCueTimeSignatureChange(index, it) },
                onDurationChange = { viewModel.onCueDurationChange(index, it) },
                onDurationTypeChange = { viewModel.onCueDurationTypeChange(index, it) },
                onPatternChange = { viewModel.onCuePatternChange(index, it) },
                dragHandleModifier = Modifier.draggableHandle(
                    onDragStopped = { viewModel.onItemMoveFinished() },
                ),
            )
        }
    }
}

private val TEMPO_OFFSET_RANGE = -20..20

@ScreenPreview
@Composable
private fun Preview() = ClickTrackTheme {
    EditClickTrackScreenView(
        viewModel = object : EditClickTrackViewModel {
            override val state: StateFlow<EditClickTrackState?> = MutableStateFlow(
                PREVIEW_CLICK_TRACK_1.toEditState(showForwardButton = true),
            )

            override fun onBackClick() = Unit

            override fun onForwardClick() = Unit

            override fun onNameChange(name: String) = Unit

            override fun onLoopChange(loop: Boolean) = Unit

            override fun onTempoOffsetChange(offset: Int) = Unit

            override fun onAddNewCueClick() = Unit

            override fun onCueRemove(index: Int) = Unit

            override fun onCueNameChange(index: Int, name: String) = Unit

            override fun onCueBpmChange(index: Int, bpm: Int) = Unit

            override fun onCueTimeSignatureChange(index: Int, timeSignature: TimeSignature) = Unit

            override fun onCueDurationChange(index: Int, duration: CueDuration) = Unit

            override fun onCueDurationTypeChange(index: Int, durationType: CueDuration.Type) = Unit

            override fun onCuePatternChange(index: Int, pattern: NotePattern) = Unit

            override fun onItemMove(from: Int, to: Int) = Unit

            override fun onItemMoveFinished() = Unit
        },
    )
}
