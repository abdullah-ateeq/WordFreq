package com.example.wordfreq

import com.example.wordfreq.analyzer.WordFrequencyAnalyzer
import com.example.wordfreq.data.model.ApostropheMode
import com.example.wordfreq.data.model.HyphenMode
import com.example.wordfreq.data.model.SortMode
import com.example.wordfreq.data.model.WordFrequencySettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Locale

class WordFrequencyAnalyzerTest {

    @Test
    fun testBasicFrequency() {
        val input = "hello hello world hello"
        val settings = WordFrequencySettings()
        val result = WordFrequencyAnalyzer.analyze(input, settings)

        assertEquals(4, result.validWordsCount)
        assertEquals(2, result.uniqueWordsCount)

        val helloFreq = result.results.first { it.word == "hello" }
        val worldFreq = result.results.first { it.word == "world" }

        assertEquals(3, helloFreq.frequency)
        assertEquals(1, worldFreq.frequency)
    }

    @Test
    fun testCaseSensitivity() {
        val input = "Hello hello HELLO"

        // Case insensitive
        val insensitiveResult = WordFrequencyAnalyzer.analyze(
            input,
            WordFrequencySettings(caseSensitive = false)
        )
        assertEquals(1, insensitiveResult.uniqueWordsCount)
        assertEquals(3, insensitiveResult.results.first().frequency)
        assertEquals("hello", insensitiveResult.results.first().word)

        // Case sensitive
        val sensitiveResult = WordFrequencyAnalyzer.analyze(
            input,
            WordFrequencySettings(caseSensitive = true)
        )
        assertEquals(3, sensitiveResult.uniqueWordsCount)
        val words = sensitiveResult.results.map { it.word }.toSet()
        assertEquals(setOf("Hello", "hello", "HELLO"), words)
        assertTrue(sensitiveResult.results.all { it.frequency == 1 })
    }

    @Test
    fun testMinimumWordLength() {
        val input = "a an the cat dog"
        val settings = WordFrequencySettings(minimumWordLength = 3)
        val result = WordFrequencyAnalyzer.analyze(input, settings)

        assertEquals(3, result.validWordsCount)
        assertEquals(3, result.uniqueWordsCount)
        val words = result.results.map { it.word }.toSet()
        assertEquals(setOf("the", "cat", "dog"), words)
    }

    @Test
    fun testStopWords() {
        val input = "the cat and the dog"
        val settings = WordFrequencySettings(stopWordsEnabled = true)
        val result = WordFrequencyAnalyzer.analyze(input, settings)

        assertEquals(2, result.validWordsCount)
        val words = result.results.map { it.word to it.frequency }
        assertEquals(setOf("cat" to 1, "dog" to 1), words.toSet())
    }

    @Test
    fun testCustomStopWords() {
        val input = "apple banana apple orange"
        val settings = WordFrequencySettings(customStopWords = setOf("apple"))
        val result = WordFrequencyAnalyzer.analyze(input, settings)

        assertEquals(2, result.validWordsCount)
        val words = result.results.map { it.word to it.frequency }
        assertEquals(setOf("banana" to 1, "orange" to 1), words.toSet())
    }

    @Test
    fun testPercentages() {
        val input = "apple apple banana"
        val settings = WordFrequencySettings()
        val result = WordFrequencyAnalyzer.analyze(input, settings)

        val apple = result.results.first { it.word == "apple" }
        val banana = result.results.first { it.word == "banana" }

        assertEquals(66.67, String.format(Locale.US, "%.2f", apple.percentage).toDouble(), 0.01)
        assertEquals(33.33, String.format(Locale.US, "%.2f", banana.percentage).toDouble(), 0.01)
    }

    @Test
    fun testAlphabeticalSort() {
        val input = "zebra apple mango"
        val settings = WordFrequencySettings()
        val result = WordFrequencyAnalyzer.analyze(
            text = input,
            settings = settings,
            sortMode = SortMode.ALPHABETICAL,
            sortDescending = false // A -> Z
        )

        val words = result.results.map { it.word }
        assertEquals(listOf("apple", "mango", "zebra"), words)
    }

    @Test
    fun testFrequencySort() {
        val input = "apple banana banana orange orange orange"
        val settings = WordFrequencySettings()
        val result = WordFrequencyAnalyzer.analyze(
            text = input,
            settings = settings,
            sortMode = SortMode.FREQUENCY,
            sortDescending = true
        )

        val words = result.results.map { it.word }
        assertEquals(listOf("orange", "banana", "apple"), words)
        assertEquals(3, result.results[0].frequency)
        assertEquals(2, result.results[1].frequency)
        assertEquals(1, result.results[2].frequency)
    }

    @Test
    fun testTieBreaking() {
        val input = "banana apple orange"
        val settings = WordFrequencySettings()
        val result = WordFrequencyAnalyzer.analyze(
            text = input,
            settings = settings,
            sortMode = SortMode.FREQUENCY,
            sortDescending = true
        )

        val words = result.results.map { it.word }
        assertEquals(listOf("apple", "banana", "orange"), words)
    }

    @Test
    fun testNoStemming() {
        val input = "run running runs"
        val settings = WordFrequencySettings()
        val result = WordFrequencyAnalyzer.analyze(input, settings)

        assertEquals(3, result.validWordsCount)
        assertEquals(3, result.uniqueWordsCount)
        val words = result.results.map { it.word to it.frequency }.toMap()
        assertEquals(1, words["run"])
        assertEquals(1, words["running"])
        assertEquals(1, words["runs"])
    }

    @Test
    fun testEmptyInput() {
        val input = ""
        val settings = WordFrequencySettings()
        val result = WordFrequencyAnalyzer.analyze(input, settings)

        assertEquals(0, result.validWordsCount)
        assertEquals(0, result.uniqueWordsCount)
        assertTrue(result.results.isEmpty())
    }

    @Test
    fun testLargeTextInput() {
        // Generate a 120,000 character text input
        val chunk = "The quick brown fox jumps over the lazy dog. "
        val repeatCount = 120_000 / chunk.length + 1
        val largeText = chunk.repeat(repeatCount)

        val settings = WordFrequencySettings()
        val result = WordFrequencyAnalyzer.analyze(largeText, settings)

        assertTrue(result.validWordsCount > 20_000)
        assertTrue(result.uniqueWordsCount >= 8)
        assertEquals("the", result.results.first().word) // 'the' appears twice per chunk
    }
}
