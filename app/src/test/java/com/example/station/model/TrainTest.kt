package com.example.station.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TrainTest {

    private val train1 = Train(1, "S", timetable = listOf(
        TimetableRow("A", 100, "5"),
        TimetableRow("B", 200, "1"),
        TimetableRow("C", 300, "3")
    ))

    private val train2 = Train(2, "IC", timetable = emptyList())

    @Test
    fun `origin returns the station code of the first timetable row`() {
        val result = train1.origin()
        assertThat(result).isEqualTo("A")
    }

    @Test
    fun `origin returns null for a train with empty timetable`() {
        val result = train2.origin()
        assertThat(result).isNull()
    }

    @Test
    fun `destination returns the station code of the last timetable row`() {
        val result = train1.destination()
        assertThat(result).isEqualTo("C")
    }

    @Test
    fun `destination returns null for a train with empty timetable`() {
        val result = train2.destination()
        assertThat(result).isNull()
    }

    @Test
    fun `track returns the track number for the given station`() {
        val result = train1.track(200)
        assertThat(result).isEqualTo("1")
    }

    @Test
    fun `track returns null for a station not in the timetable`() {
        val result = train1.track(5)
        assertThat(result).isNull()
    }
}