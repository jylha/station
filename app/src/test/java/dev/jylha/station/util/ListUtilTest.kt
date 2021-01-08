package dev.jylha.station.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test


class ListUtilTest {

    private val testList = listOf(1, 2, 3, 4)
    private val testPredicate: (Int) -> Boolean =  { it % 2 == 0 }

    @Test fun `filter list when condition is true`() {
        val result = testList.filterWhen(true, predicate = testPredicate)
        assertThat(result).isEqualTo(listOf(2, 4))
    }

    @Test fun `filter list when condition is false`() {
        val result = testList.filterWhen(false, predicate = testPredicate)
        assertThat(result).isEqualTo(testList)
    }
}
