package com.suchongan.battery.ui.history

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suchongan.battery.R
import com.suchongan.battery.data.db.BatterySample
import com.suchongan.battery.ui.theme.ChartAxisTextDark
import com.suchongan.battery.ui.theme.ChartAxisTextLight
import com.suchongan.battery.ui.theme.ChartGridLineDark
import com.suchongan.battery.ui.theme.ChartGridLineLight
import kotlin.math.roundToInt

private val Y_AXIS_LABEL_WIDTH = 34.dp
private val AXIS_LABEL_GAP = 8.dp
private val CHART_X_AXIS_INDENT = Y_AXIS_LABEL_WIDTH + AXIS_LABEL_GAP
private val Y_AXIS_LEVELS = listOf(100, 75, 50, 25, 0)

/**
 * Minimal custom line chart of battery level (%) over time, with a Y-axis percentage
 * column. Deliberately avoids a third-party charting dependency to keep the build
 * graph small for an MVP. Pair with [ChartXAxisLabels] below it for a time axis.
 */
@Composable
fun BatteryLineChart(samples: List<BatterySample>, modifier: Modifier = Modifier) {
    val lineColor = MaterialTheme.colorScheme.primary
    val gridColor = chartGridLineColor()
    val axisTextColor = chartAxisTextColor()

    Row(modifier = modifier) {
        Column(
            modifier = Modifier
                .width(Y_AXIS_LABEL_WIDTH)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Y_AXIS_LEVELS.forEach { level ->
                Text(text = "$level%", fontSize = 11.sp, color = axisTextColor)
            }
        }
        Spacer(modifier = Modifier.width(AXIS_LABEL_GAP))
        Canvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        ) {
            if (samples.size < 2) return@Canvas

            val minTime = samples.first().timestampMillis.toFloat()
            val maxTime = samples.last().timestampMillis.toFloat()
            val timeRange = (maxTime - minTime).coerceAtLeast(1f)

            val gridStrokeWidth = 1.dp.toPx()
            Y_AXIS_LEVELS.forEach { level ->
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
}

/** Unit granularity for [relativeChartXAxisLabels]. */
enum class ChartTimeUnit(val labelResId: Int) {
    MINUTES(R.string.chart_x_axis_minutes_ago),
    HOURS(R.string.chart_x_axis_hours_ago),
    DAYS(R.string.chart_x_axis_days_ago),
}

/**
 * Builds 5 evenly-spaced "N [unit] ago" labels counting back from now to [totalUnits] ago,
 * e.g. totalUnits=24 with [ChartTimeUnit.HOURS] yields 24/18/12/6/0 hours ago (0 -> "現在").
 */
@Composable
fun relativeChartXAxisLabels(totalUnits: Int, unit: ChartTimeUnit): List<String> {
    val nowLabel = stringResource(R.string.chart_x_axis_now)
    return listOf(0f, 0.25f, 0.5f, 0.75f, 1f).map { frac ->
        val value = (totalUnits * (1f - frac)).roundToInt()
        if (value <= 0) nowLabel else stringResource(unit.labelResId, value)
    }
}

/** X-axis tick row for [BatteryLineChart], indented to align under the chart (not the Y-axis labels). */
@Composable
fun ChartXAxisLabels(labels: List<String>, modifier: Modifier = Modifier) {
    val axisTextColor = chartAxisTextColor()
    Row(
        modifier = modifier.padding(start = CHART_X_AXIS_INDENT),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        labels.forEach { label ->
            Text(text = label, fontSize = 11.sp, color = axisTextColor)
        }
    }
}

@Composable
private fun chartAxisTextColor() = if (isSystemInDarkTheme()) ChartAxisTextDark else ChartAxisTextLight

@Composable
private fun chartGridLineColor() = if (isSystemInDarkTheme()) ChartGridLineDark else ChartGridLineLight
