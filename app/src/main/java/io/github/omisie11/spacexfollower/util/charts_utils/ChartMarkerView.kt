package io.github.omisie11.spacexfollower.util.charts_utils

import android.content.Context
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import io.github.omisie11.spacexfollower.R
import kotlin.math.roundToInt
import kotlinx.android.synthetic.main.marker_view.view.*

class ChartMarkerView(context: Context, layoutResource: Int) : MarkerView(context, layoutResource) {

    // callbacks every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    override fun refreshContent(entry: Entry, highlight: Highlight) {
        text_chart_marker_month.text = convertNumberToMonthShort(entry.x.roundToInt())
        text_chart_marker_number.text = entry.y.roundToInt().toString()

        // this will perform necessary layouting
        super.refreshContent(entry, highlight)
    }

    override fun getOffset(): MPPointF = MPPointF((-(width / 2)).toFloat(), (-height).toFloat())

    // In particular use case first month = 0, last = 11
    private fun convertNumberToMonthShort(number: Int): String = when (number) {
        0 -> resources.getString(R.string.month_short_january)
        1 -> resources.getString(R.string.month_short_february)
        2 -> resources.getString(R.string.month_short_march)
        3 -> resources.getString(R.string.month_short_april)
        4 -> resources.getString(R.string.month_may)
        5 -> resources.getString(R.string.month_short_june)
        6 -> resources.getString(R.string.month_short_july)
        7 -> resources.getString(R.string.month_short_august)
        8 -> resources.getString(R.string.month_short_september)
        9 -> resources.getString(R.string.month_short_october)
        10 -> resources.getString(R.string.month_short_november)
        11 -> resources.getString(R.string.month_short_december)
        else -> ""
    }
}
