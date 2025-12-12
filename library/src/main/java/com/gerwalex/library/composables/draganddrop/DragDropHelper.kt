package com.gerwalex.library.composables.draganddrop

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


/**
 * State manager for drag-and-drop functionality within a `LazyColumn` or `LazyRow`.
 *
 * This class holds the state related to a drag-and-drop operation, such as the currently
 * dragged item, its displacement, and the logic to handle drag events. It collaborates
 * with a [LazyListState] to get information about the list's layout and trigger scroll
 * events.
 *
 * An instance of this class is typically created and remembered using [rememberDragAndDropListState].
 *
 * @param lazyListState The state of the lazy list, used to access item positions and layout information.
 * @param onMove A callback function that is invoked when an item is successfully moved to a new position.
 *               It provides the `from` index (the original position) and the `to` index (the new position).
 *               This callback is responsible for updating the underlying data list.
 */
class DragAndDropListState(
    val lazyListState: LazyListState,
    private val onMove: (Int, Int) -> Unit
) {
    private var draggingDistance by mutableFloatStateOf(0f)
    private var initialDraggingElement by mutableStateOf<LazyListItemInfo?>(null)
    var currentIndexOfDraggedItem by mutableStateOf<Int?>(null)
    private val initialOffsets: Pair<Int, Int>?
        get() = initialDraggingElement?.let { Pair(it.offset, it.offsetEnd) }

    /**
     * The vertical displacement of the currently dragged element.
     *
     * This value is `null` if no item is being dragged. Otherwise, it represents the
     * difference between the initial drag position and the current position of the item,
     * effectively how much the item's composable should be translated on the Y-axis
     * to follow the user's finger.
     */
    val elementDisplacement: Float?
        get() = currentIndexOfDraggedItem?.let {
            lazyListState.getVisibleItemInfo(it)
        }?.let { itemInfo ->
            (initialDraggingElement?.offset ?: 0f).toFloat() + draggingDistance - itemInfo.offset
        }

    /**
     * The [LazyListItemInfo] of the item currently being dragged.
     * This is `null` if no item is being dragged.
     */
    private val currentElement: LazyListItemInfo?
        get() = currentIndexOfDraggedItem?.let {
            lazyListState.getVisibleItemInfo(it)
        }

    /**
     * Handles the initiation of a drag gesture.
     *
     * This function is called when a long-press gesture is detected. It identifies which
     * list item is being dragged based on the initial touch offset and sets up the
     * initial state for the drag operation.
     *
     * @param offset The position of the initial touch event within the lazy list's coordinate space.
     */
    fun onDragStart(offset: Offset) {
        lazyListState.layoutInfo.visibleItemsInfo
            .firstOrNull { item -> offset.y.toInt() in item.offset..item.offsetEnd }
            ?.also {
                initialDraggingElement = it
                currentIndexOfDraggedItem = it.index
            }
    }

    fun onDragInterrupted() {
        initialDraggingElement = null
        currentIndexOfDraggedItem = null
        draggingDistance = 0f
    }

    /**
     * Handles the movement of the dragged item.
     *
     * This function is called continuously as the user drags their finger. It updates the
     * `draggingDistance` and calculates the new position of the dragged item. If the item
     * is dragged over another item, it triggers the `onMove` callback to reorder the list
     * and updates the `currentIndexOfDraggedItem`.
     *
     * @param offset The change in position since the last drag event.
     */
    fun onDrag(offset: Offset) {
        draggingDistance += offset.y

        initialOffsets?.let { (top, bottom) ->
            val startOffset = top.toFloat() + draggingDistance
            val endOffset = bottom.toFloat() + draggingDistance

            currentElement?.let { current ->
                lazyListState.layoutInfo.visibleItemsInfo
                    .filterNot { item ->
                        item.offsetEnd < startOffset || item.offset > endOffset || current.index == item.index
                    }
                    .firstOrNull { item ->
                        val delta = startOffset - current.offset
                        when {
                            delta < 0 -> item.offset > startOffset
                            else -> item.offsetEnd < endOffset
                        }
                    }
            }?.also { item ->
                currentIndexOfDraggedItem?.let { current ->
                    onMove.invoke(current, item.index)
                }
                currentIndexOfDraggedItem = item.index
            }
        }
    }

    fun checkOverscroll(): Float {
        return initialDraggingElement?.let {
            val startOffset = it.offset + draggingDistance
            val endOffset = it.offsetEnd + draggingDistance

            return@let when {
                draggingDistance > 0 -> {
                    (endOffset - lazyListState.layoutInfo.viewportEndOffset).takeIf { diff -> diff > 0 }

                }

                draggingDistance < 0 -> {
                    (startOffset - lazyListState.layoutInfo.viewportStartOffset).takeIf { diff -> diff < 0 }
                }

                else -> null
            }
        } ?: 0f
    }

    private fun LazyListState.getVisibleItemInfo(itemPosition: Int): LazyListItemInfo? {
        return this.layoutInfo.visibleItemsInfo.getOrNull(itemPosition - this.firstVisibleItemIndex)
    }

    private val LazyListItemInfo.offsetEnd: Int
        get() = this.offset + this.size
}

/**
 * Creates and remembers a [DragAndDropListState] for a lazy list.
 * This state is essential for managing the drag-and-drop behavior, including tracking
 * the dragged item, its position, and triggering move callbacks.
 *
 * @param lazyListState The [LazyListState] of the `LazyColumn` or `LazyRow` to which
 *                      drag-and-drop functionality is being added. This is used to access
 *                      layout information and control scrolling.
 * @param onMove A callback function that is invoked when a dragged item is moved to a new
 *               position. It provides the 'from' and 'to' indices, allowing you to update
 *               your underlying data source accordingly.
 * @return A remembered instance of [DragAndDropListState].
 */
@Composable
fun rememberDragAndDropListState(
    lazyListState: LazyListState,
    onMove: (Int, Int) -> Unit
): DragAndDropListState {
    return remember { DragAndDropListState(lazyListState, onMove) }
}

fun <T> MutableList<T>.move(from: Int, to: Int) {
    if (from == to) return
    val element = this.removeAt(from)
    this.add(to, element)
}

/**
 * A modifier that enables drag-and-drop functionality for a `LazyColumn` or `LazyRow`.
 * It detects long-press gestures to initiate a drag, handles the drag events, and manages
 * auto-scrolling when the dragged item reaches the edges of the viewport.
 *
 * This modifier should be applied to the `LazyColumn` or `LazyRow` composable itself.
 *
 * @param dragAndDropListState The state object that manages the drag-and-drop operation.
 * @param overscrollJob A reference to a `Job` that can be used to manage the overscroll coroutine.
 * @param coroutineScope The `CoroutineScope` to launch the overscroll job in.
 * @return A `Modifier` that adds the drag-and-drop detection logic.
 */
fun Modifier.dragContainer(
    dragAndDropListState: DragAndDropListState,
    overscrollJob: Job?,
    coroutineScope: CoroutineScope
): Modifier {
    var coroutineJob = overscrollJob
    return this
        .pointerInput(Unit) {
            detectDragGesturesAfterLongPress(
                onDrag = { change, offset ->
                    change.consume()
                    dragAndDropListState.onDrag(offset)

                    if (overscrollJob?.isActive == true) return@detectDragGesturesAfterLongPress

                    dragAndDropListState
                        .checkOverscroll()
                        .takeIf { it != 0f }
                        ?.let {
                            coroutineJob = coroutineScope.launch {
                                dragAndDropListState.lazyListState.scrollBy(it)
                            }
                        } ?: run { coroutineJob?.cancel() }

                },
                onDragStart = { offset ->
                    dragAndDropListState.onDragStart(offset)
                },
                onDragEnd = { dragAndDropListState.onDragInterrupted() },
                onDragCancel = { dragAndDropListState.onDragInterrupted() }
            )
        }
}

fun Modifier.dragVisuals(
    dragAndDropListState: DragAndDropListState,
    index: Int
): Modifier {
    // 1. Logik zur Bestimmung des Offsets und des Drag-Zustands
    val offsetOrNull = dragAndDropListState.elementDisplacement.takeIf {
        index == dragAndDropListState.currentIndexOfDraggedItem
    }
    val isDragging = offsetOrNull != null

    // 2. Anwenden der visuellen Effekte
    return this
        .zIndex(if (isDragging) 1f else 0f) // Braucht 'Modifier.zIndex'
        .graphicsLayer { // Braucht 'Modifier.graphicsLayer'
            // Der Translation-Wert muss in der Lambda-Funktion geholt werden,
            // da er sich während des Composables ändern kann.
            translationY = offsetOrNull ?: 0f
            scaleX = if (isDragging) .9f else 1f
            scaleY = if (isDragging) .9f else 1f
        }
}

