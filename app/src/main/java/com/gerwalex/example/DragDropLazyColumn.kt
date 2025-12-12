package com.gerwalex.example

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gerwalex.library.composables.draganddrop.dragContainer
import com.gerwalex.library.composables.draganddrop.dragVisuals
import com.gerwalex.library.composables.draganddrop.move
import com.gerwalex.library.composables.draganddrop.rememberDragAndDropListState
import kotlinx.coroutines.Job

data class DragDropListItem(
    val id: Long,
    val text: String,
)


@Composable
fun DragDropLazyColumnExample() {
    val list = mutableStateListOf<DragDropListItem>()
    for (index in 0..20L) {
        list.add(DragDropListItem(index, "Item $index"))
    }
    DragDropLazyColumnExample(list)
}


@Composable
fun DragDropLazyColumnExample(list: MutableList<DragDropListItem>) {
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
            DragDropListItem(
                item = item,
                modifier =
                    Modifier
                        .dragVisuals(dragAndDropListState, index)
                        .animateItem()
            )
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