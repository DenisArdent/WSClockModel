import androidx.compose.animation.core.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

fun Modifier.dashedBorder(
    color: Color,
    strokeWidth: Dp,
    strokeLength: Dp,
    animate: Boolean = true,
) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }
        val strokeLengthPx = density.run { strokeLength.toPx() }
        // store the last animate value be next animate start
        var lastAnimValue by remember { mutableStateOf(0f) }
        val anim = remember(animate) { Animatable(lastAnimValue) }

        LaunchedEffect(animate) {
            if (animate) {
                anim.animateTo(
                    // Important !!! Animate Target need add the lastAnim value, Simple math knowledge :)
                    (strokeLengthPx * 2 + lastAnimValue), animationSpec =
                    infiniteRepeatable(
                        animation = tween(300, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart,
                    )
                ) {
                    lastAnimValue = value // store the anim value
                }
            }
        }

        this.then(
            Modifier.drawWithCache {
                onDrawBehind {
                    val stroke = Stroke(
                        width = strokeWidthPx,
                        pathEffect = PathEffect.dashPathEffect(
                            intervals = floatArrayOf(strokeLengthPx, strokeLengthPx),
                            phase = anim.value, // always use the anim
                        )
                    )
                    drawRect(
                        color = color,
                        style = stroke,
                    )
                }
            }
        )
    }
)