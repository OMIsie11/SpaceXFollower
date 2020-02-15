package io.github.omisie11.spacexfollower

import io.github.omisie11.spacexfollower.util.shortenNumberAddPrefix
import org.junit.Assert.assertEquals
import org.junit.Test

class NumbersUtilsTestsShortenNumbers {

    // Tests for shortenNumberAddPrefix function
    @Test
    fun shortenLessThanMillions() {
        // Numbers < million are shown as they are (not needed, function for only converting company valuation)
        assertEquals("900000", shortenNumberAddPrefix(900000))
        assertEquals("9", shortenNumberAddPrefix(9))
    }

    @Test
    fun shortenMillions() {
        // Function should shorten the number (3 points) and add 'millions', return String
        assertEquals("3 million", shortenNumberAddPrefix(3000000))
        assertEquals("37 million", shortenNumberAddPrefix(37000000))
        assertEquals("999 million", shortenNumberAddPrefix(999000000))

        // Test not round numbers
        assertEquals("37.5 million", shortenNumberAddPrefix(37500000))
        assertEquals("37.99 million", shortenNumberAddPrefix(37990000))
        assertEquals("38 million", shortenNumberAddPrefix(37999000))
    }

    @Test
    fun shortenBillions() {
        // Function should shorten the number (3 points) and add 'millions', return String
        assertEquals("3 billion", shortenNumberAddPrefix(3000000000))
        assertEquals("37 billion", shortenNumberAddPrefix(37000000000))
        assertEquals("999 billion", shortenNumberAddPrefix(999000000000))

        // Test not round numbers
        assertEquals("37.5 billion", shortenNumberAddPrefix(37500000000))
        assertEquals("37.99 billion", shortenNumberAddPrefix(37990000000))
        assertEquals("38 billion", shortenNumberAddPrefix(37999000000))
    }
}