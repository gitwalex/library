package com.gerwalex.library.composables.animation

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.util.lerp
import com.gerwalex.library.R
import kotlin.math.min

@Composable
fun ImageFadeInOutAnimation(painter: Painter, modifier: Modifier = Modifier) {
    AnimatedContent(
        targetState = painter, modifier = modifier, transitionSpec = {
            (fadeIn(tween(easing = LinearEasing)) + scaleIn(
                tween(
                    1_000, easing = LinearEasing
                )
            )).togetherWith(
                fadeOut(
                    tween(
                        1_000, easing = LinearEasing
                    )
                ) + scaleOut(
                    tween(
                        1_000, easing = LinearEasing
                    )
                )
            )
        }, label = ""
    ) { pntr ->
        Image(
            painter = pntr, modifier = Modifier.fillMaxSize(0.8f), contentDescription = ""
        )
    }
}

@Composable
fun ImageFadeInOutAnimated(painter: Painter, modifier: Modifier = Modifier) {
    var transition = remember {
        0f
    }
    LaunchedEffect(key1 = painter, block = {
        transition = lerp(0f, 100f, 0.1f)
    })


    Image(painter = painter,
        modifier = Modifier
            .scale(.8f + (.2f * transition))
            .graphicsLayer { rotationX = (1f - transition) * 5f }
            .alpha(min(1f, transition / .2f))
            .fillMaxSize(0.8f),
        contentDescription = "")
}

@Preview(name = "Light", uiMode = UI_MODE_NIGHT_NO)
@Composable
fun ImageFadeInOutPreview() {
    val resources = LocalContext.current.resources
    val bitmap = BitmapFactory.decodeResource(
        resources, R.drawable.demo_image
    )
    Surface {
        ImageFadeInOutAnimated(painter = BitmapPainter(bitmap.asImageBitmap()))
    }

}
