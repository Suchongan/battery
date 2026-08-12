package com.suchongan.battery.ui.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.suchongan.battery.R
import kotlin.math.cos
import kotlin.math.sin

/**
 * A semicircular needle gauge (like a speedometer's top half). The needle position and the
 * displayed number both animate from the same interpolated value, so the readout visibly
 * counts toward the new value instead of snapping — matches the needle motion.
 *
 * [value] of null renders a dimmed, needle-at-rest gauge with an "unavailable" label, for
 * properties the device doesn't report (e.g. charging current on some devices).
 */
@Composable
fun DialGauge(
    value: Float?,
    valueRange: ClosedFloatingPointRange<Float>,
    label: String,
    formatValue: (Float) -> String,
    modifier: Modifier = Modifier,
) {
    val animatedValue by animateFloatAsState(
        targetValue = value ?: valueRange.start,
        animationSpec = tween(durationMillis = 600),
        label = "gaugeValue",
    )
    val fraction = if (value == null) {
        0f
    } else {
        ((animatedValue - valueRange.start) / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)
    }

    val activeColor = if (value == null) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Canvas(modifier = Modifier.size(width = 76.dp, height = 46.dp)) {
            val strokeWidth = 8.dp.toPx()
            val radius = (size.width - strokeWidth) / 2f
            val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)
            val pivot = Offset(topLeft.x + radius, topLeft.y + radius)
            val arcSize = Size(radius * 2, radius * 2)

            drawArc(
                color = trackColor,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
            if (fraction > 0f) {
                drawArc(
                    color = activeColor,
                    startAngle = 180f,
                    sweepAngle = 180f * fraction,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                )
            }

            val needleAngleRad = Math.toRadians((180f + 180f * fraction).toDouble())
            val needleLength = radius - strokeWidth
            val needleEnd = Offset(
                x = pivot.x + (needleLength * cos(needleAngleRad)).toFloat(),
                y = pivot.y + (needleLength * sin(needleAngleRad)).toFloat(),
            )
            drawLine(color = activeColor, start = pivot, end = needleEnd, strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
            drawCircle(color = activeColor, radius = 4.dp.toPx(), center = pivot)
        }
        Text(
            text = if (value == null) stringResource(R.string.dashboard_value_unavailable) else formatValue(animatedValue),
            style = MaterialTheme.typography.labelLarge,
        )
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
