package dev.jylha.station.util

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.Instant
import org.junit.Test

class DateTimeUtilTest {

    @Test fun `differsFrom() returns false for the same times`() {
        val dt1 = Instant.parse("2020-01-01T10:00:00.000Z")
        val result = dt1.differsFrom(dt1)
        assertThat(result).isFalse()
    }

    @Test fun `differsFrom() returns false for times differing by less than a minute`() {
        val dt1 = Instant.parse("2020-01-01T10:00:30.000Z")
        val dt2 = Instant.parse("2020-01-01T10:01:29.000Z")
        val result = dt1.differsFrom(dt2)
        assertThat(result).isFalse()
    }

    @Test fun `differsFrom() returns true for times when other is more than minute later`() {
        val time = Instant.parse("2020-01-01T10:00:30.000Z")
        val other = Instant.parse("2020-01-01T10:01:30.000Z")
        val result = time.differsFrom(other)
        assertThat(result).isTrue()
    }

    @Test fun `differsFrom() returns true for times when other is more than minute earlier`() {
        val time = Instant.parse("2020-01-01T10:01:30.000Z")
        val other = Instant.parse("2020-01-01T10:00:30.000Z")
        val result = time.differsFrom(other)
        assertThat(result).isTrue()
    }
}
