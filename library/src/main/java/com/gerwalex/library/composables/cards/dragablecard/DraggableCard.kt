package com.gerwalex.library.composables.cards.dragablecard

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.gerwalex.library.ext.toPx
import kotlin.math.roundToInt

const val ANIMATION_DURATION = 500
const val MIN_DRAG_AMOUNT = 6


class DraggableCardState(
    isRevealed: Boolean,
    cardOffset: Dp,
) {
    var dragAmount by mutableStateOf(0f)
    var isRevealed by mutableStateOf(isRevealed)
    var cardOffset by mutableFloatStateOf(cardOffset.toPx)
}

@Composable
fun rememberDraggableCardState(
    isRevealed: Boolean = false,
    cardOffset: Dp = 100.dp,
): DraggableCardState {
    return remember { DraggableCardState(isRevealed, cardOffset) }

}


@Composable
fun DraggableCard(
    state: DraggableCardState,
    modifier: Modifier = Modifier,
    onExpand: () -> Unit,
    onCollapse: () -> Unit,
    cardExpandedBackgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    cardCollapsedBackgroundColor: Color = MaterialTheme.colorScheme.surface,
    actionCard: @Composable () -> Unit,
    cardContent: @Composable () -> Unit,
) {
    val cardBgColor by animateColorAsState(
        if (state.isRevealed) cardExpandedBackgroundColor else cardCollapsedBackgroundColor
    )
    val offsetTransition by animateFloatAsState(if (state.isRevealed) state.cardOffset else 0f)
    val cardElevation by animateDpAsState(
        animationSpec = tween(1000),
        targetValue = if (state.isRevealed) 20.dp else 2.dp
    )

    Box(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize())
        {
            actionCard()
        }
        Card(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(offsetTransition.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        state.dragAmount = dragAmount
                        when {
                            dragAmount >= MIN_DRAG_AMOUNT -> onExpand()

                            dragAmount < -MIN_DRAG_AMOUNT -> onCollapse()
                        }
                    }
                },
            shape = remember { RoundedCornerShape(0.dp) },
            colors = CardDefaults.cardColors(containerColor = cardBgColor),
            elevation = CardDefaults.cardElevation(cardElevation),
            content = { cardContent() }
        )
    }
}

