package com.gerwalex.example

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class DragDropListItem(
    val id: Long,
    val text: String,
)


class DragAndDropListState(
    val lazyListState: LazyListState,
    private val onMove: (Int, Int) -> Unit
) {
    private var draggingDistance by mutableFloatStateOf(0f)
    private var initialDraggingElement by mutableStateOf<LazyListItemInfo?>(null)
    var currentIndexOfDraggedItem by mutableStateOf<Int?>(null)
    private val initialOffsets: Pair<Int, Int>?
        get() = initialDraggingElement?.let { Pair(it.offset, it.offsetEnd) }
    val elementDisplacement: Float?
        get() = currentIndexOfDraggedItem?.let {
            lazyListState.getVisibleItemInfo(it)
        }?.let { itemInfo ->
            (initialDraggingElement?.offset ?: 0f).toFloat() + draggingDistance - itemInfo.offset
        }
    private val currentElement: LazyListItemInfo?
        get() = currentIndexOfDraggedItem?.let {
            lazyListState.getVisibleItemInfo(it)
        }

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

@Composable
fun DragDropLazyColumnExmple() {
    val list = mutableStateListOf<DragDropListItem>()
    for (index in 0..20L) {
        list.add(DragDropListItem(index, "Item $index"))
    }
    DragDropLazyColumnExmple(list)
}

fun Modifier.dragContainer(
    dragAndDropListState: DragAndDropListState,
    overscrollJob: Job?,
    coroutineScope: CoroutineScope
): Modifier {
    var coroutineJob = overscrollJob
    return this.pointerInput(Unit) {
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
                    } ?: kotlin.run { coroutineJob?.cancel() }

            },
            onDragStart = { offset ->
                dragAndDropListState.onDragStart(offset)
            },
            onDragEnd = { dragAndDropListState.onDragInterrupted() },
            onDragCancel = { dragAndDropListState.onDragInterrupted() }
        )
    }
}

@Composable
fun LazyItemScope.DraggableItem(
    dragAndDropListState: DragAndDropListState,
    index: Int,
    content: @Composable LazyItemScope.(Modifier) -> Unit
) {
    val draggingModifier = Modifier
        .composed {
            val offsetOrNull =
                dragAndDropListState.elementDisplacement.takeIf {
                    index == dragAndDropListState.currentIndexOfDraggedItem
                }
            Modifier.graphicsLayer {
                translationY = offsetOrNull ?: 0f
            }
        }
    content(draggingModifier)
}

@Composable
fun DragDropLazyColumnExmple(list: MutableList<DragDropListItem>) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var overscrollJob by remember { mutableStateOf<Job?>(null) }
    val dragAndDropListState =
        rememberDragAndDropListState(listState) { from, to ->
            list.move(from, to)
        }
    LazyColumn(
        modifier = Modifier
            .padding(8.dp)
            .dragContainer(
                dragAndDropListState = dragAndDropListState,
                overscrollJob = overscrollJob,
                coroutineScope = coroutineScope
            ),
        state = dragAndDropListState.lazyListState
    ) {
        itemsIndexed(items = list, key = { _, item -> item.id }) { index, item: DragDropListItem ->
            DraggableItem(dragAndDropListState, index) {
                DragDropListItem(
                    item = item,
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}

@Composable
fun DragDropListItem(item: DragDropListItem, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Text(text = item.text)
        Text(text = item.text)
    }
}