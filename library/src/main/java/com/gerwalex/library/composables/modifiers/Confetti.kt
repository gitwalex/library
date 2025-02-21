package com.gerwalex.library.composables.modifiers

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import com.gerwalex.library.composables.modifiers.ConfettiState.Companion.sizeChanged
import kotlinx.coroutines.isActive
import kotlin.math.roundToInt

val RAINBOW = listOf(
    Color(0xFF33004c), Color(0xFF4600d2),
    Color(0xFF0000ff), Color(0xFF0099ff),
    Color(0xFF00eeff), Color(0xFF00FF7F),
    Color(0xFF48FF00), Color(0xFFB6FF00),
    Color(0xFFFFD700), Color(0xFFff9500),
    Color(0xFFFF6200), Color(0xFFFF0000),
    Color(0xFF33004c)
)


/**
 * Represents the shape of a confetti piece.
 */
fun Modifier.confetti(
    contentColors: List<Color> = RAINBOW,
    confettiShape: ConfettiShape = ConfettiShape.Mixed,
    speed: Float = 0.2f,
    populationFactor: Float = 0.1f,
    isVisible: Boolean = true
) = composed {
    var confettiState by remember {
        mutableStateOf(
            ConfettiState(
                confetti = emptyList(),
                speed = speed,
                colors = contentColors,
                confettiShape = confettiShape
            )
        )
    }

    var lastFrame by remember { mutableLongStateOf(-1L) }

    LaunchedEffect(isVisible) {
        while (isVisible && isActive) {
            withFrameMillis { newTick ->
                val elapsedMillis = newTick - lastFrame
                val wasFirstFrame = lastFrame < 0
                lastFrame = newTick
                if (wasFirstFrame) return@withFrameMillis

                for (confetto in confettiState.confetti) {
                    confettiState.next(elapsedMillis)
                }
            }
        }
    }

    onSizeChanged {
        confettiState = confettiState.sizeChanged(
            size = it,
            populationFactor = populationFactor
        )
    }.drawBehind {
        if (isVisible) {
            for (confetto in confettiState.confetti) {
                confetto.draw(drawContext.canvas)
            }
        }
    }
}

enum class ConfettiShape {
    Mixed,
    Rectangle,
    Circle
}

/**
 * Represents the state of the confetti animation.
 *
 * This data class holds the current state of the confetti particles, including their
 * positions, colors, shapes, the size of the confetti area, and the speed of the particles.
 *
 * @property confetti A list of [Confetto] objects representing the individual confetti particles.
 * @property colors A list of [Color] objects that will be used to color the confetti particles.
 * @property confettiShape The shape of the confetti particles, represented by a [ConfettiShape] object.
 * @property size The [IntSize] of the area where the confetti is displayed. This determines the
 *                 boundaries within which confetti particles will move.
 * @property speed The speed at which the confetti particles will move. Higher values result in faster movement.
 */
data class ConfettiState(
    val confetti: List<Confetto> = emptyList(),
    val colors: List<Color>,
    val confettiShape: ConfettiShape,
    val size: IntSize = IntSize.Zero,
    val speed: Float
) {

    fun next(durationMillis: Long) {
        confetti.forEach {
            it.next(size, durationMillis, speed)
        }
    }

    companion object {
        fun ConfettiState.sizeChanged(
            size: IntSize,
            populationFactor: Float
        ): ConfettiState {
            if (size == this.size) return this
            return copy(
                confetti = (0..size.realPopulation(populationFactor)).map {
                    Confetto.create(size, colors, confettiShape)
                },
                size = size
            )
        }

        private fun IntSize.realPopulation(populationFactor: Float): Int {
            return (width * height / 10_000 * populationFactor).roundToInt()
        }
    }
}

class Confetto(
    vector: Offset,
    private val confettiColor: Color,
    private val radius: Float,
    private val shape: ConfettiShape = ConfettiShape.Circle,
    position: Offset
) {
    internal var position by mutableStateOf(position)
    private var vector by mutableStateOf(vector)
    private val paint: Paint = Paint().apply {
        isAntiAlias = true
        color = confettiColor
        style = PaintingStyle.Fill
    }

    fun next(
        borders: IntSize,
        durationMillis: Long,
        speedCoefficient: Float
    ) {
        val speed = vector * speedCoefficient
        val borderTop = 0
        val borderLeft = 0
        val borderBottom = borders.height
        val borderRight = borders.width

        position = Offset(
            x = position.x + (speed.x / 1000f * durationMillis),
            y = position.y + (speed.y / 1000f * durationMillis),
        )
        val vx = if (position.x < borderLeft || position.x > borderRight) -vector.x else vector.x
        val vy = if (position.y < borderTop || position.y > borderBottom) -vector.y else vector.y

        if (vx != vector.x || vy != vector.y) {
            vector = Offset(vx, vy)
        }
    }

    fun draw(canvas: Canvas) {

        when (shape) {
            ConfettiShape.Circle -> {
                canvas.drawCircle(
                    radius = radius,
                    center = position,
                    paint = paint
                )
            }

            ConfettiShape.Rectangle -> {
                val rect = Rect(position.x, position.y, position.x + radius, position.y + radius)
                canvas.drawRect(
                    rect = rect,
                    paint = paint
                )
            }

            ConfettiShape.Mixed -> TODO()
        }

    }

    companion object {

        fun create(borders: IntSize, colors: List<Color>, confettiShape: ConfettiShape): Confetto {
            val shape = if (confettiShape == ConfettiShape.Mixed) {
                if ((0..1).random() == 0) ConfettiShape.Circle else ConfettiShape.Rectangle
            } else confettiShape
            return Confetto(
                position = Offset(
                    (0..borders.width).random().toFloat(),
                    (0..borders.height).random().toFloat()
                ),
                vector = Offset(
                    // First, randomize direction. Second, randomize amplitude of speed vector.
                    listOf(
                        -1f,
                        1f
                    ).random() * ((borders.width.toFloat() / 100f).toInt()..(borders.width.toFloat() / 10f).toInt()).random()
                        .toFloat(),
                    listOf(
                        -1f,
                        1f
                    ).random() * ((borders.height.toFloat() / 100f).toInt()..(borders.height.toFloat() / 10f).toInt()).random()
                        .toFloat()
                ),
                confettiColor = colors.random(),
                radius = (5..25).random().toFloat(),
                shape = shape
            )
        }
    }
}
