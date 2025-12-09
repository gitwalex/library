package com.gerwalex.example

import android.content.ClipData
import android.util.Log
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class DragDropListItem(
    val id: Long,
    val text: String,
)

@Composable
fun DragDropLazyColumnExmple() {
    val list = mutableListOf<DragDropListItem>()
    for (index in 0..20L) {
        list.add(DragDropListItem(index, "Item $index"))
    }
    DragDropLazyColumnExmple(list)
}

@Composable
fun DragDropLazyColumnExmple(list: MutableList<DragDropListItem>) {
    var backgroundColor by remember { mutableStateOf(Color.Transparent) }
    var dragedItem by remember { mutableStateOf<DragDropListItem?>(null) }
    val callback = remember {
        object : DragAndDropTarget {
            override fun onStarted(event: DragAndDropEvent) {
                backgroundColor = Color.DarkGray.copy(alpha = 0.2f)
            }

            override fun onDrop(event: DragAndDropEvent): Boolean {
//                onDragAndDropEventDropped(event)
                return true
            }

            override fun onEnded(event: DragAndDropEvent) {
                backgroundColor = Color.Transparent
            }

        }
    }
    LaunchedEffect(dragedItem) {
        Log.d("WPPaketList: ", "draged: $dragedItem")
    }
    LazyColumn(
        modifier = Modifier
            .padding(8.dp)
            .dragAndDropTarget(
                shouldStartDragAndDrop = { true },
                target = callback
            )
    ) {
        items(items = list, key = { it.id }) { item: DragDropListItem ->
            DragDropListItem(
                item = item,
                modifier = Modifier
                    .background(backgroundColor)
                    .dragAndDropSource { offset ->
                        dragedItem = item
                        DragAndDropTransferData(
                            clipData = ClipData.newPlainText("dragDrop", "Label2"),
                            flags = View.DRAG_FLAG_GLOBAL,
                        )
                    })
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
    }
}