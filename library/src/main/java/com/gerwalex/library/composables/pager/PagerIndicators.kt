@file:OptIn(ExperimentalFoundationApi::class)

package com.gerwalex.library.composables.pager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import kotlin.math.absoluteValue

fun PagerState.offsetForPage(page: Int) = (currentPage - page) + currentPageOffsetFraction

// NEW FUNCTION FOR INDICATORS
fun PagerState.indicatorOffsetForPage(page: Int) =
    1f - offsetForPage(page).coerceIn(-1f, 1f).absoluteValue


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SimplePagerIndicators(state: PagerState) {
    Row(
        Modifier
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(state.pageCount) { iteration ->
            val color =
                if (state.currentPage == iteration)
                    MaterialTheme.colorScheme.onSurface else
                    MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.5f
                    )
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(16.dp)

            )
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SquareIndicator(
    modifier: Modifier = Modifier,
    state: PagerState,
    color: Color,
) {
    Row(
        modifier = modifier
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        for (i in 0 until state.pageCount) {
            Box(
                modifier = Modifier.size(32.dp),
                contentAlignment = Alignment.Center
            ) {
                val offset = state.indicatorOffsetForPage(i)
                Box(
                    Modifier
                        .rotate(135f * offset)
                        .size(
                            lerp(12.dp, 22.dp, offset)
                        )
                        .border(
                            width = 3.dp,
                            color = color,
                            shape = RectangleShape,
                        )
                )
            }
        }
    }
}

@Composable
fun CircleIndicator(
    state: PagerState,
    color: Color,
    size: Dp,
    modifier: Modifier = Modifier,

    ) {
    Row(
        modifier = modifier
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        for (i in 0 until state.pageCount) {
            val offset = state.indicatorOffsetForPage(i)
            Box(
                modifier = Modifier.size(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    Modifier
                        .size(
                            lerp(6.dp, size, offset)
                        )
                        .border(
                            width = 2.dp,
                            color = color,
                            shape = CircleShape,
                        )
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LineIndicator(
    modifier: Modifier = Modifier,
    state: PagerState,
    color: Color,
) {
    Row(
        modifier = modifier
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        for (i in 0 until state.pageCount) {
            val offset = state.indicatorOffsetForPage(i)
            Box(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .weight(.5f + (offset * 3f))
                    .height(8.dp)
                    .background(
                        color = color,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {

            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GlowIndicator(
    modifier: Modifier = Modifier,
    state: PagerState,
    color: Color,
) {
    Row(
        modifier = modifier
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        for (i in 0 until state.pageCount) {
            val offset = state.indicatorOffsetForPage(i)
            Box(
                modifier = Modifier
                    .size(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    Modifier
                        .size(
                            lerp(14.dp, 32.dp, offset)
                        )
                        .blur(
                            radius = lerp(0.dp, 16.dp, offset),
                            edgeTreatment = BlurredEdgeTreatment.Unbounded,
                        )
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Cyan,
                                    Color.Magenta,
                                )
                            ),
                            shape = CircleShape
                        )
                )
                Box(
                    Modifier
                        .size(
                            lerp(14.dp, 22.dp, offset)
                        )
                        .background(
                            color = color,
                            shape = CircleShape,
                        )
                )
            }
        }
    }
}