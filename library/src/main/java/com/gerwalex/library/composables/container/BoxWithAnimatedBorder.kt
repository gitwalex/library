package com.gerwalex.library.compose.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A Composable that displays a box with an animated border.
 * The border animates by rotating a sweep gradient.
 *
 * @param modifier Modifier to be applied to the outer Surface.
 * @param borderSize The thickness of the animated border. Defaults to 5.dp.
 * @param shape The shape of the box and its border. Defaults to a RoundedCornerShape with 5.dp radius.
 * @param tween The duration of one full rotation of the border animation in milliseconds. Defaults to 3500.
 * @param borderColors A list of colors to be used in the sweep gradient for the border.
 *                     If empty, a default gradient of Gray and White will be used.
 * @param content The composable content to be displayed inside the box.
 */
@Composable
fun BoxWithAnimatedBorder(
    modifier: Modifier = Modifier,
    borderSize: Dp = 5.dp,
    shape: RoundedCornerShape = RoundedCornerShape(5.dp),
    tween: Int = 3500,
    borderColors: List<Color> = emptyList(),
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val angle by
    infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(tween, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ), label = "CardWithAnimatedBorder"
    )

    val brush =
        if (borderColors.isNotEmpty()) Brush.sweepGradient(borderColors)
        else Brush.sweepGradient(listOf(Color.Gray, Color.White))

    Surface(
        modifier = modifier,
        shape = shape
    ) {
        Surface(
            modifier =
                Modifier
                    .clipToBounds()
                    .fillMaxWidth()
                    .padding(borderSize)
                    .drawWithContent {
                        rotate(angle) {
                            drawCircle(
                                brush = brush,
                                radius = size.width,
                                blendMode = BlendMode.SrcIn,
                            )
                        }
                        drawContent()
                    },
            shape = shape
        ) {
            Card(modifier = Modifier.padding(8.dp)) { content() }
        }
    }
}

@Preview
@Composable
private fun BoxWithAnimatedBorderPreview() {
    BoxWithAnimatedBorder(
        content = {
            Text("Card with animated border")
        }
    )
}

