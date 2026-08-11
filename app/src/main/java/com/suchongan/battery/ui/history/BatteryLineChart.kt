package com.suchongan.battery.ui.history

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.suchongan.battery.data.db.BatterySample

/**
 * Minimal custom line chart of battery level (%) over time. Deliberately avoids a
 * third-party charting dependency to keep the build graph small for an MVP.
 */
@Composable
fun BatteryLineChart(samples: List<BatterySample>, modifier: Modifier = Modifier) {
    val lineColor = MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.outlineVariant

    Canvas(modifier = modifier) {
        if (samples.size < 2) return@Canvas

        val minTime = samples.first().timestampMillis.toFloat()
        val maxTime = samples.last().timestampMillis.toFloat()
        val timeRange = (maxTime - minTime).coerceAtLeast(1f)

        val gridStrokeWidth = 1.dp.toPx()
        listOf(0, 25, 50, 75, 100).forEach { level ->
            val y = size.height - (level / 100f) * size.height
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = gridStrokeWidth,
            )
        }

        val path = Path()
        samples.forEachIndexed { index, sample ->
            val x = ((sample.timestampMillis - minTime) / timeRange) * size.width
            val y = size.height - (sample.level / 100f) * size.height
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}
