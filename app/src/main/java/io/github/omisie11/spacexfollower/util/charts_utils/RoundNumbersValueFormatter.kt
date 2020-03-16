package io.github.omisie11.spacexfollower.util.charts_utils

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.math.roundToInt

class RoundNumbersValueFormatter : ValueFormatter() {

    override fun getAxisLabel(value: Float, axis: AxisBase?): String =
        value.roundToInt().toString()

    override fun getFormattedValue(value: Float): String =
        value.roundToInt().toString()
}
