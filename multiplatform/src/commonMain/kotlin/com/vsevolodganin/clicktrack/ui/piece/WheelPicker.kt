package com.vsevolodganin.clicktrack.ui.piece

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseOutQuad
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastSumBy
import androidx.compose.ui.util.lerp
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.abs

/**
 * A composable function that displays a vertical scrolling picker allowing users to choose
 * from a list of items.
 *
 * @param T The type of the items to be displayed in the picker.
 * @param selectedIndex The index of the currently selected item. This controls the initial
 * position of the picker and can be updated to animate a selection change.
 * @param items A list of items to display in the picker.
 * @param onItemSelect A callback invoked whenever an item is selected. Provides the selected
 * index and item.
 * @param modifier A [Modifier] for styling and configuring the appearance and behavior
 * of the picker.
 * @param itemContent A composable lambda that describes how to render each item in the list. Provides
 * the item's index, value, whether it is selected, and its closeness to the center of the picker
 * (to animate stuff like alpha, scale, etc.).
 */
@Composable
fun <T> WheelPicker(
    selectedIndex: Int,
    items: List<T>,
    onItemSelect: (index: Int, item: T) -> Unit,
    modifier: Modifier = Modifier,
    itemContent: @Composable LazyItemScope.(index: Int, item: T, isSelected: Boolean, closenessToSelection: Float) -> Unit =
        { _, item, isSelected, closenessToSelection ->
            DefaultWheelItemContent(item, isSelected, closenessToSelection)
        },
) {
    val lazyListState = rememberLazyListState(selectedIndex)
    val itemIndexInCenter by itemIndexInCenter(lazyListState)
    val contentPadding by contentPadding(lazyListState)
    val snapBehaviour = rememberSnapFlingBehaviorWithOnFinishedListener(lazyListState) {
        onItemSelect(itemIndexInCenter, items[itemIndexInCenter])
    }

    LaunchedEffect(selectedIndex) {
        lazyListState.animateScrollToItem(selectedIndex)
    }

    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        contentPadding = contentPadding,
        flingBehavior = snapBehaviour,
    ) {
        itemsIndexed(items) { index, item ->
            val closenessToSelection by closenessToSelection(index, lazyListState)
            itemContent(
                index,
                item,
                index == itemIndexInCenter,
                closenessToSelection,
            )
        }
    }
}

@Composable
fun <T> LazyItemScope.DefaultWheelItemContent(item: T, isSelected: Boolean, closenessToSelection: Float) {
    val easedClosenessToSelection = EaseOutQuad.transform(closenessToSelection)
    val scale = lerp(0.0f, 1f, easedClosenessToSelection)
    val color by animateColorAsState(if (isSelected) MaterialTheme.colors.primary else LocalContentColor.current)

    Text(
        text = item.toString(),
        modifier = Modifier
            .fillParentMaxWidth()
            .scale(scale)
            .alpha(easedClosenessToSelection),
        color = color,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun rememberSnapFlingBehaviorWithOnFinishedListener(
    lazyListState: LazyListState,
    onSnapFinished: () -> Unit,
): FlingBehavior {
    val flingBehaviour = rememberSnapFlingBehavior(lazyListState)
    return remember(flingBehaviour, onSnapFinished) {
        object : FlingBehavior {
            override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
                return with(flingBehaviour) { performFling(initialVelocity) }
                    .also { onSnapFinished() }
            }
        }
    }
}

@Composable
private fun itemIndexInCenter(lazyListState: LazyListState): State<Int> {
    return remember {
        derivedStateOf {
            val layout = lazyListState.layoutInfo
            val viewportStart = layout.viewportStartOffset
            val viewportEnd = layout.viewportEndOffset
            val viewportCenter = (viewportStart + viewportEnd) / 2

            layout.visibleItemsInfo.minByOrNull { info ->
                val itemCenter = info.offset + info.size / 2
                abs(itemCenter - viewportCenter)
            }?.index ?: 0
        }
    }
}

@Composable
private fun contentPadding(lazyListState: LazyListState): State<PaddingValues> {
    val density = LocalDensity.current
    return remember {
        derivedStateOf {
            val layout = lazyListState.layoutInfo
            val verticalPaddingPx = (layout.viewportSize.height - layout.visibleItemsAverageSize()) / 2
            val verticalPaddingDp = with(density) { verticalPaddingPx.toDp() }

            PaddingValues(vertical = verticalPaddingDp)
        }
    }
}

@Composable
private fun closenessToSelection(itemIndex: Int, lazyListState: LazyListState): State<Float> {
    return remember {
        derivedStateOf {
            val layout = lazyListState.layoutInfo
            val viewportStart = layout.viewportStartOffset
            val viewportEnd = layout.viewportEndOffset
            val viewportCenter = (viewportStart + viewportEnd) / 2

            lazyListState.layoutInfo.visibleItemsInfo
                .firstOrNull { it.index == itemIndex }
                ?.let { info ->
                    val itemCenter = info.offset + info.size / 2
                    abs(itemCenter - viewportCenter)
                }
                ?.let { distanceFromCenter ->
                    1f - distanceFromCenter.toFloat() / layout.viewportSize.height * 2
                }
                ?: 0f
        }
    }
}

private fun LazyListLayoutInfo.visibleItemsAverageSize(): Int {
    return if (visibleItemsInfo.isEmpty()) {
        0
    } else {
        val visibleItems = visibleItemsInfo
        val itemsSum = visibleItems.fastSumBy { it.size }
        itemsSum / visibleItems.size + mainAxisItemSpacing
    }
}

@Preview
@Composable
private fun WheelPickerPreview() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        WheelPicker(
            selectedIndex = 100,
            items = List(300) { index -> "Item $index" },
            onItemSelect = { _, _ -> },
            modifier = Modifier.fillMaxHeight(0.5f),
        )

        // Guidelines
        Box(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(Color.Gray.copy(alpha = 0.2f)),
        )
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(Color.Gray.copy(alpha = 0.2f)),
        )
    }
}
