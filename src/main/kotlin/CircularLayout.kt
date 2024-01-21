import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun CircularLayout(
    radius: Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var circleRadiusPx = with(LocalDensity.current) { radius.roundToPx() }
    var totalRadius = 0
    var maxChildDiameter = 0
    var count = 0
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }
        count = placeables.size
        placeables.forEach {
            val h = it.height.toDouble()
            val w = it.width.toDouble()
            val diameter = sqrt(h * h + w * w)
            if (diameter > maxChildDiameter) maxChildDiameter = diameter.toInt()
        }
        totalRadius = circleRadiusPx + maxChildDiameter / 2
        layout(totalRadius * 2, totalRadius * 2) {
            val step = PI * 2 / count
            var angle = 0.0
            placeables.forEach { placeable ->
                placeable.place(
                    totalRadius - placeable.width / 2 + (circleRadiusPx * cos(angle)).toInt(),
                    totalRadius - placeable.height / 2 + (circleRadiusPx * sin(angle)).toInt(),
                )
                angle += step
            }
        }
    }
}
