package com.gerwalex.library.modifier

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toOffset
import com.gerwalex.library.R

/**
 *  @see <a href="https://medium.com/@kappdev/wobbly-coin-animation-in-jetpack-compose-491e51129a96">Wobbly Coin Animation</a>
 */
fun Modifier.wobblyEffect(
    modifier: Modifier = Modifier,
    maxRotationAngle: Float = 20f,
    onPress: ((Offset) -> Unit)? = null
) = composed {
    var pressOffset by remember { mutableStateOf<Offset?>(null) }

    val animatedOffset = pressOffset?.let { offset ->
        animateOffsetAsState(
            targetValue = offset,
            animationSpec = spring(stiffness = Spring.StiffnessHigh),
            label = "Press Offset Animation"
        )
    }

    this
        .graphicsLayer {
            if (animatedOffset != null) {
                val fractionX = (animatedOffset.value.y - size.center.y) / size.center.y
                val fractionY = (animatedOffset.value.x - size.center.x) / size.center.x

                val rotationX = -fractionX * maxRotationAngle
                val rotationY = fractionY * maxRotationAngle

                this.rotationX = rotationX.coerceIn(-maxRotationAngle, maxRotationAngle)
                this.rotationY = rotationY.coerceIn(-maxRotationAngle, maxRotationAngle)

                cameraDistance = (10f * density)
            }
        }
        .then(modifier)
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = { offset ->
                    pressOffset = offset
                    onPress?.invoke(offset)
                    tryAwaitRelease()
                    pressOffset = size.center.toOffset()
                }
            )
        }
}

@Preview(name = "Light", uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun WobblyEffectPreview() {
    Surface {
        Image(
            painter = painterResource(id = R.drawable.demo_image), contentDescription = null,
            modifier = Modifier.wobblyEffect(modifier = Modifier.shadow(16.dp, CircleShape))
        )
    }
}