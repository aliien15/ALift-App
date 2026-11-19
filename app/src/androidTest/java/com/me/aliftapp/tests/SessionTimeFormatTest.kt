package com.me.aliftapp.tests

import com.me.aliftapp.ui.workout.formatSessionTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class SessionTimeFormatTest {

    @Test
    fun formatSessionTimeFormatsMinutesAndSecondsCorrectly() {
        val startTime = 0L
        val currentTime = 125000L // 2 minutes and 5 seconds

        val result = formatSessionTime(startTime, currentTime)

        assertEquals("02:05", result)
    }

    @Test
    fun formatSessionTimeFormatsHoursCorrectly() {
        val startTime = 0L
        val currentTime = 3665000L // 1 hour, 1 minute, 5 seconds

        val result = formatSessionTime(startTime, currentTime)

        assertEquals("01:01:05", result)
    }

    @Test
    fun formatSessionTimeThrowsExceptionWhenStartTimeIsGreaterThanCurrentTime() {
        val startTime = 5000L
        val currentTime = 0L

        // Verifies that the strict contract halts execution and throws the expected error
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                formatSessionTime(startTime, currentTime)
            }

        assertEquals(
            "Contract Violation: startTime (5000) cannot be greater than currentTime (0)",
            exception.message,
        )
    }
}
