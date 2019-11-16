package io.github.omisie11.spacexfollower.data.charts_formatters

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

class MonthsValueFormatter(
    private val months: List<String> = listOf(
        "Jan.", "Feb.", "Mar.", "Apr.", "May", "Jun.", "Jul.",
        "Aug.", "Sep.", "Oct.", "Nov.", "Dec."
    )
) : ValueFormatter() {

    override fun getAxisLabel(value: Float, axis: AxisBase?): String =
        months.getOrNull(value.toInt()) ?: value.toString()
}