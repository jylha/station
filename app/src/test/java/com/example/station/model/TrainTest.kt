package com.example.station.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TrainTest {

    private val train1 = Train(1, "S", timetable = listOf(
        TimetableRow("A", 100, "5"),
        TimetableRow("B", 200, "5"),
        TimetableRow("C", 300, "5")
    ))

    private val train2 = Train(2, "IC", timetable = emptyList())

    @Test
    fun `origin returns the station code of the first timetable row`() {
        val result = train1.origin()
        assertThat(result).isEqualTo("A")
    }

    @Test
    fun `origin return null for a train with empty timetable`() {
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
}