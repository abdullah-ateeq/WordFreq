package com.example.wordfreq

import com.example.wordfreq.data.model.WordFreqHistory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HistoryLogicTest {

    @Test
    fun testConsecutiveDuplicateComparison() {
        val entry1 = WordFreqHistory(
            id = "1",
            text = "Hello world",
            analyzedAt = 1000L,
            caseSensitive = false,
            includeNumbers = true,
            apostropheMode = "PRESERVE",
            hyphenMode = "KEEP",
            stopWordsEnabled = false,
            minimumWordLength = 1,
            maximumWordLength = null
        )

        // Exact same config and text, different id and timestamp
        val entry2 = WordFreqHistory(
            id = "2",
            text = "Hello world",
            analyzedAt = 2000L,
            caseSensitive = false,
            includeNumbers = true,
            apostropheMode = "PRESERVE",
            hyphenMode = "KEEP",
            stopWordsEnabled = false,
            minimumWordLength = 1,
            maximumWordLength = null
        )

        // Different text
        val entry3 = entry1.copy(id = "3", text = "Different text")

        fun isConsecutiveDuplicate(latest: WordFreqHistory, next: WordFreqHistory): Boolean {
            return latest.text == next.text &&
                    latest.caseSensitive == next.caseSensitive &&
                    latest.includeNumbers == next.includeNumbers &&
                    latest.apostropheMode == next.apostropheMode &&
                    latest.hyphenMode == next.hyphenMode &&
                    latest.stopWordsEnabled == next.stopWordsEnabled &&
                    latest.minimumWordLength == next.minimumWordLength &&
                    latest.maximumWordLength == next.maximumWordLength
        }

        assertTrue(isConsecutiveDuplicate(entry1, entry2))
        assertFalse(isConsecutiveDuplicate(entry1, entry3))
    }

    @Test
    fun testMax10HistoryLimit() {
        val list = mutableListOf<WordFreqHistory>()
        for (i in 1..15) {
            val entry = WordFreqHistory(
                id = "$i",
                text = "Text $i",
                analyzedAt = i.toLong(),
                caseSensitive = false,
                includeNumbers = true,
                apostropheMode = "PRESERVE",
                hyphenMode = "KEEP",
                stopWordsEnabled = false,
                minimumWordLength = 1,
                maximumWordLength = null
            )
            list.add(0, entry)
            while (list.size > 10) {
                list.removeAt(list.lastIndex)
            }
        }

        assertEquals(10, list.size)
        // Latest entry should be Text 15
        assertEquals("Text 15", list.first().text)
        // Oldest entry retained should be Text 6
        assertEquals("Text 6", list.last().text)
    }
}
