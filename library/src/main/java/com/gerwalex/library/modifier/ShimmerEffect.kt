package com.gerwalex.library.modifier

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

/**
 * Applies a shimmer effect to a composable.
 *
 * This modifier creates a shimmering animation that can be applied to any composable.
 * It achieves this by creating a linear gradient that moves horizontally across the composable's
 * background, giving the illusion of a "shine" or shimmer.
 *
 * The animation consists of a gradient moving from left to right, repeating infinitely.
 * The gradient colors are a light gray, dark gray, and light gray, creating a subtle shimmer effect.
 * The animation speed and shimmer width can be adjusted by changing the `tween` duration and number of times of the size width, respectively.
 *
 * The shimmer effect is clipped to a rounded rectangle shape with a corner radius of 6.dp.
 *
 * @return A `Modifier` that applies the shimmer effect.
 *
 * Example usage:
 * ```kotlin
 * Box(modifier = Modifier
 *     .fillMaxWidth()
 *     .height(100.dp)
 *     .shimmerEffect()) {
 *     // Content to be shimmered
 *     Text("Shimmering Text", color = Color.White)
 * }
 * ```
 */
@Composable
fun Modifier.shimmerEffect(): Modifier {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val transition = rememberInfiniteTransition(
        label = "Animates the background"
    )
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000)
        ),
        label = "Animates the background"
    )
    return this then (Modifier
        .background(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFB8B5B5),
                    Color(0xFF8F8B8B),
                    Color(0xFFB8B5B5),
                ),
                start = Offset(startOffsetX, 0f),
                end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
            ),
            shape = RoundedCornerShape(6.dp)
        )
        .onGloballyPositioned {
            size = it.size
        }
            )
}

@Preview(name = "Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ShimmerEffectPreview() {
    Surface {
        Text(
            text = "Dies ist ein langer Text",
            modifier = Modifier
                .fillMaxWidth()
                .shimmerEffect()
        )
    }
}
