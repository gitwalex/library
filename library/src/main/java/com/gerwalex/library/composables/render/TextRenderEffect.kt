package com.gerwalex.library.composables.render

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.gerwalex.library.composables.container.BlurContainer
import com.gerwalex.library.composables.container.ShaderContainer

/**
 * @see  <a href="https://canopas.com/how-to-use-render-effects-in-jetpack-compose-for-stunning-visuals-01287d7f00db">Blured TextViewChange</a>
 */

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun TextRenderEffect(
    animateTextList: List<String> = listOf(
        "\"Reach your goals\"",
        "\"Achieve your dreams\"",
        "\"Be happy\"",
        "\"Be healthy\"",
        "\"Get rid of depression\"",
        "\"Overcome loneliness\""
    ),
    spec: AnimationSpec<Int> = tween(durationMillis = 3000, easing = LinearEasing),

    ) {

    var textToDisplay by remember {
        mutableStateOf("")
    }

    val blur = remember { Animatable(0f) }

    LaunchedEffect(textToDisplay) {
        blur.animateTo(30f, tween(easing = LinearEasing))
        blur.animateTo(0f, tween(easing = LinearEasing))
    }
    // Animatable index to control the progress of the animation
    val index = remember {
        Animatable(initialValue = 0, typeConverter = Int.VectorConverter)
    }
    LaunchedEffect(key1 = animateTextList) {
        textToDisplay = animateTextList[index.value]
        index.animateTo(animateTextList.size, spec)
    }

    ShaderContainer(
        modifier = Modifier.fillMaxSize()
    ) {
        BlurContainer(
            modifier = Modifier.fillMaxSize(),
            blur = blur.value,
            component = {
                AnimatedContent(
                    targetState = textToDisplay,
                    modifier = Modifier
                        .fillMaxWidth(),
                    transitionSpec = {
                        (scaleIn()).togetherWith(
                            scaleOut()
                        )
                    }, label = ""
                ) { text ->
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = text,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                }
            }
        ) {}
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@PreviewLightDark
@Composable
fun TextRenderEffectPreview() {
    Surface {
        TextRenderEffect()
    }

}