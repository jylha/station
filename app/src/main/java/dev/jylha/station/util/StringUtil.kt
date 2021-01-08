package dev.jylha.station.util

/**
 * Finds all instances of the given [substring] within the string.
 * @param substring The substring to be searched from the string.
 * @param ignoreCase Determines whether case is ignored when matching the string.
 * @return List of start and end index pairs for found substrings.
 */
fun String.findAllMatches(substring: String, ignoreCase: Boolean = true): List<Pair<Int, Int>> {
    val list = mutableListOf<Pair<Int, Int>>()
    if (substring.isNotEmpty()) {
        var startIndex = 0
        while (startIndex < length) {
            val index = indexOf(substring, startIndex, ignoreCase)
            startIndex = if (index >= 0) {
                val endIndex = index + substring.length
                list += Pair(index, endIndex)
                endIndex
            } else {
                length
            }
        }
    }
    return list
}

/**
 * Inserts spaces between each character of the string (for example, "ABC" -> "A B C").
 * @receiver The original string
 * @return New string with spaces inserted into the original string.
 */
fun String.insertSpaces() : String {
    return foldIndexed("") { index, acc, ch ->
        if (index != lastIndex) "$acc$ch " else "$acc$ch"
    }
}
