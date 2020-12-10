package com.example.station.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test


class StringUtilTest {

    @Test fun `searching substring from a blank string returns empty list`() {
        val result = "".findAllMatches("abc")
        assertThat(result).isEmpty()
    }

    @Test fun `searching a blank substring from a string returns empty list`() {
        val result = "abc".findAllMatches("")
        assertThat(result).isEmpty()
    }

    @Test fun `searching a string from itself returns list with single match`() {
        val result = "abc".findAllMatches("abc")
        assertThat(result).isEqualTo(listOf(Pair(0, 3)))
    }

    @Test fun `search for a substring with single match`() {
        val result = "babcab".findAllMatches("abc")
        assertThat(result).isEqualTo(listOf(Pair(1, 4)))
    }

    @Test fun `search for a substring with two matches`() {
        val result = "abcdabcd".findAllMatches("abc")
        assertThat(result).isEqualTo(listOf(Pair(0, 3), Pair(4, 7)))
    }

    @Test fun `search for a substring does not return overlapping matches`() {
        val result = "aaaaa".findAllMatches("aa")
        assertThat(result).isEqualTo(listOf(Pair(0, 2), Pair(2, 4)))
    }

    @Test fun `search for a substring with exact case`() {
        val result = "abcAbc".findAllMatches("Abc", ignoreCase = false)
        assertThat(result).isEqualTo(listOf(Pair(3, 6)))
    }

    @Test fun `search for a substring by ignoring case`() {
        val result = "abcAbc".findAllMatches("Abc", ignoreCase = true)
        assertThat(result).isEqualTo(listOf(Pair(0, 3), Pair(3, 6)))
    }

    @Test fun `insert spaces into an empty string`() {
        val result = "".insertSpaces()
        assertThat(result).isEqualTo("")
    }

    @Test fun `insert spaces into a string of one char`() {
        val result = "a".insertSpaces()
        assertThat(result).isEqualTo("a")
    }

    @Test fun `insert spaces into a string with one word`() {
        val result = "word".insertSpaces()
        assertThat(result).isEqualTo("w o r d")
    }
}
