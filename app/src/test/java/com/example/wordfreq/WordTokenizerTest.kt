package com.example.wordfreq

import com.example.wordfreq.data.model.ApostropheMode
import com.example.wordfreq.data.model.HyphenMode
import com.example.wordfreq.tokenizer.WordTokenizer
import org.junit.Assert.assertEquals
import org.junit.Test

class WordTokenizerTest {

    @Test
    fun testBasicWhitespaceAndPunctuation() {
        val input = "hello, hello! \"hello\"."
        val tokens = WordTokenizer.tokenize(
            text = input,
            caseSensitive = false,
            includeNumbers = true,
            apostropheMode = ApostropheMode.PRESERVE,
            hyphenMode = HyphenMode.KEEP
        )
        assertEquals(listOf("hello", "hello", "hello"), tokens)
    }

    @Test
    fun testMultipleWhitespace() {
        val input = "hello    hello\n\nhello"
        val tokens = WordTokenizer.tokenize(
            text = input,
            caseSensitive = false,
            includeNumbers = true,
            apostropheMode = ApostropheMode.PRESERVE,
            hyphenMode = HyphenMode.KEEP
        )
        assertEquals(listOf("hello", "hello", "hello"), tokens)
    }

    @Test
    fun testCaseInsensitive() {
        val input = "Hello hello HELLO"
        val tokens = WordTokenizer.tokenize(
            text = input,
            caseSensitive = false,
            includeNumbers = true,
            apostropheMode = ApostropheMode.PRESERVE,
            hyphenMode = HyphenMode.KEEP
        )
        assertEquals(listOf("hello", "hello", "hello"), tokens)
    }

    @Test
    fun testCaseSensitive() {
        val input = "Hello hello HELLO"
        val tokens = WordTokenizer.tokenize(
            text = input,
            caseSensitive = true,
            includeNumbers = true,
            apostropheMode = ApostropheMode.PRESERVE,
            hyphenMode = HyphenMode.KEEP
        )
        assertEquals(listOf("Hello", "hello", "HELLO"), tokens)
    }

    @Test
    fun testApostrophePreserve() {
        val input = "don't don't can't"
        val tokens = WordTokenizer.tokenize(
            text = input,
            caseSensitive = false,
            includeNumbers = true,
            apostropheMode = ApostropheMode.PRESERVE,
            hyphenMode = HyphenMode.KEEP
        )
        assertEquals(listOf("don't", "don't", "can't"), tokens)
    }

    @Test
    fun testApostropheSplit() {
        val input = "don't"
        val tokens = WordTokenizer.tokenize(
            text = input,
            caseSensitive = false,
            includeNumbers = true,
            apostropheMode = ApostropheMode.SPLIT,
            hyphenMode = HyphenMode.KEEP
        )
        assertEquals(listOf("don", "t"), tokens)
    }

    @Test
    fun testApostropheRemove() {
        val input = "don't"
        val tokens = WordTokenizer.tokenize(
            text = input,
            caseSensitive = false,
            includeNumbers = true,
            apostropheMode = ApostropheMode.REMOVE,
            hyphenMode = HyphenMode.KEEP
        )
        assertEquals(listOf("dont"), tokens)
    }

    @Test
    fun testUnicodeApostrophe() {
        val input = "don’t" // U+2019
        val tokens = WordTokenizer.tokenize(
            text = input,
            caseSensitive = false,
            includeNumbers = true,
            apostropheMode = ApostropheMode.PRESERVE,
            hyphenMode = HyphenMode.KEEP
        )
        assertEquals(listOf("don't"), tokens)
    }

    @Test
    fun testHyphenKeep() {
        val input = "well-known well-known"
        val tokens = WordTokenizer.tokenize(
            text = input,
            caseSensitive = false,
            includeNumbers = true,
            apostropheMode = ApostropheMode.PRESERVE,
            hyphenMode = HyphenMode.KEEP
        )
        assertEquals(listOf("well-known", "well-known"), tokens)
    }

    @Test
    fun testHyphenSplit() {
        val input = "well-known"
        val tokens = WordTokenizer.tokenize(
            text = input,
            caseSensitive = false,
            includeNumbers = true,
            apostropheMode = ApostropheMode.PRESERVE,
            hyphenMode = HyphenMode.SPLIT
        )
        assertEquals(listOf("well", "known"), tokens)
    }

    @Test
    fun testHyphenRemove() {
        val input = "well-known"
        val tokens = WordTokenizer.tokenize(
            text = input,
            caseSensitive = false,
            includeNumbers = true,
            apostropheMode = ApostropheMode.PRESERVE,
            hyphenMode = HyphenMode.REMOVE
        )
        assertEquals(listOf("wellknown"), tokens)
    }

    @Test
    fun testNumbersIncludedAndExcluded() {
        val input = "2026 2026 100 hello"
        val tokensIncluded = WordTokenizer.tokenize(
            text = input,
            caseSensitive = false,
            includeNumbers = true,
            apostropheMode = ApostropheMode.PRESERVE,
            hyphenMode = HyphenMode.KEEP
        )
        assertEquals(listOf("2026", "2026", "100", "hello"), tokensIncluded)

        val tokensExcluded = WordTokenizer.tokenize(
            text = input,
            caseSensitive = false,
            includeNumbers = false,
            apostropheMode = ApostropheMode.PRESERVE,
            hyphenMode = HyphenMode.KEEP
        )
        assertEquals(listOf("hello"), tokensExcluded)
    }

    @Test
    fun testAlphanumericTokens() {
        val input = "AI2 Version2 abc123"
        val tokens = WordTokenizer.tokenize(
            text = input,
            caseSensitive = false,
            includeNumbers = false, // Even if numbers are excluded, mixed alphanumeric should be preserved!
            apostropheMode = ApostropheMode.PRESERVE,
            hyphenMode = HyphenMode.KEEP
        )
        assertEquals(listOf("ai2", "version2", "abc123"), tokens)
    }

    @Test
    fun testUrlAndEmailFiltering() {
        val urlInput = "Visit https://example.com today"
        val urlTokens = WordTokenizer.tokenize(
            text = urlInput,
            caseSensitive = false,
            includeNumbers = true,
            apostropheMode = ApostropheMode.PRESERVE,
            hyphenMode = HyphenMode.KEEP,
            ignoreUrls = true
        )
        assertEquals(listOf("visit", "today"), urlTokens)

        val emailInput = "Contact test@example.com today"
        val emailTokens = WordTokenizer.tokenize(
            text = emailInput,
            caseSensitive = false,
            includeNumbers = true,
            apostropheMode = ApostropheMode.PRESERVE,
            hyphenMode = HyphenMode.KEEP,
            ignoreEmails = true
        )
        assertEquals(listOf("contact", "today"), emailTokens)
    }

    @Test
    fun testUnicodeSupport() {
        val input = "café café\n你好 你好\nمرحبا مرحبا"
        val tokens = WordTokenizer.tokenize(
            text = input,
            caseSensitive = false,
            includeNumbers = true,
            apostropheMode = ApostropheMode.PRESERVE,
            hyphenMode = HyphenMode.KEEP
        )
        assertEquals(listOf("café", "café", "你好", "你好", "مرحبا", "مرحبا"), tokens)
    }
}
