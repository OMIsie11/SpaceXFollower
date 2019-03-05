package io.github.omisie11.spacexfollower.util

import java.math.RoundingMode
import java.text.DecimalFormat

class NumbersUtils {
    // ToDo: Add tests
    // Used to convert SpaceX valuation, no need to support negative numbers
    fun shortenNumberAddPrefix(numberToFormat: Long): String {
        val number = numberToFormat.toDouble()
        val df = DecimalFormat("#.##").apply { roundingMode = RoundingMode.CEILING }
        return when {
            number > 1000000000 -> "${df.format(number.div(1000000000))} billion"
            number > 1000000 -> "${df.format(number.div(1000000))} million"
            else -> number.toString()
        }
    }
}