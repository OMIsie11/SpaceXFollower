package io.github.omisie11.spacexfollower.util

import org.junit.Assert.assertEquals
import org.junit.Test

class NumbersUtilsTestsDates {

    @Test
    fun getMonthValueFromUnixTime() {
        assertEquals(12, getMonthValueFromUnixTime(1577186367))
        assertEquals(8, getMonthValueFromUnixTime(1377186367))
        assertEquals(10, getMonthValueFromUnixTime(1475418636))
        assertEquals(1, getMonthValueFromUnixTime(0))
        assertEquals(12, getMonthValueFromUnixTime(-187481))
    }

    @Test
    fun getYearValueFromUnixTime() {
        assertEquals(2019, getYearValueFromUnixTime(1577186367))
        assertEquals(2013, getYearValueFromUnixTime(1377186367))
        assertEquals(2016, getYearValueFromUnixTime(1475418636))
        assertEquals(1970, getYearValueFromUnixTime(0))
        assertEquals(1969, getYearValueFromUnixTime(-187481))
    }
}