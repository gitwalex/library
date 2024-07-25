package com.gerwalex.library.composables.cards

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val cardBorderColors: List<Color> =
    listOf(
        Color(0xFFFF595A),
        Color(0xFFFFC766),
        Color(0xFF35A07F),
        Color(0xFF35A07F),
        Color(0xFFFFC766),
        Color(0xFFFF595A)
    )

@Composable
fun CardWithAnimatedBorder(
    modifier: Modifier = Modifier,
    borderSize: Dp = 5.dp,
    tween: Int = 3500,
    onCardClick: () -> Unit = {},
    borderColors: List<Color> = cardBorderColors,
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
        modifier =
        modifier.clickable {
            onCardClick()
        }, shape = RoundedCornerShape(10.dp)
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
//            color = HushTheme.colors.backgroundColors.backgroundGlassPrimary,
            shape = RoundedCornerShape(9.dp)
        ) {
            Box(modifier = Modifier.padding(8.dp)) { content() }
        }
    }
}

