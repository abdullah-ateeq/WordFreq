package com.example.wordfreq.tokenizer

import com.example.wordfreq.data.model.ApostropheMode
import com.example.wordfreq.data.model.HyphenMode
import java.util.Locale
import java.util.regex.Pattern

object WordTokenizer {

    private val URL_PATTERN = Pattern.compile("https?://[^\\s]+|www\\.[^\\s]+", Pattern.CASE_INSENSITIVE)
    private val EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
    private val UNICODE_APOSTROPHES = Pattern.compile("[’‘ʼʻ′]")
    private val UNICODE_HYPHENS = Pattern.compile("[‐‑]")
    private val DASH_SEPARATORS = Pattern.compile("[—–―]")

    // Word pattern when both apostrophe and hyphen can be internal
    private val WORD_PATTERN_BOTH = Pattern.compile("[\\p{L}\\p{M}\\p{N}]+(?:['\\-][\\p{L}\\p{M}\\p{N}]+)*")
    // Word pattern when only apostrophe can be internal
    private val WORD_PATTERN_APOSTROPHE = Pattern.compile("[\\p{L}\\p{M}\\p{N}]+(?:'[\\p{L}\\p{M}\\p{N}]+)*")
    // Word pattern when only hyphen can be internal
    private val WORD_PATTERN_HYPHEN = Pattern.compile("[\\p{L}\\p{M}\\p{N}]+(?:-[\\p{L}\\p{M}\\p{N}]+)*")
    // Word pattern when neither can be internal
    private val WORD_PATTERN_SIMPLE = Pattern.compile("[\\p{L}\\p{M}\\p{N}]+")

    /**
     * Primary tokenization function conforming to specifications.
     */
    fun tokenize(
        text: String,
        caseSensitive: Boolean,
        includeNumbers: Boolean,
        apostropheMode: ApostropheMode,
        hyphenMode: HyphenMode
    ): List<String> {
        return tokenize(
            text = text,
            caseSensitive = caseSensitive,
            includeNumbers = includeNumbers,
            apostropheMode = apostropheMode,
            hyphenMode = hyphenMode,
            ignoreUrls = true,
            ignoreEmails = true,
            preserveHashtags = false,
            preserveMentions = false
        )
    }

    /**
     * Full tokenization function with special token handling.
     */
    fun tokenize(
        text: String,
        caseSensitive: Boolean = false,
        includeNumbers: Boolean = true,
        apostropheMode: ApostropheMode = ApostropheMode.PRESERVE,
        hyphenMode: HyphenMode = HyphenMode.KEEP,
        ignoreUrls: Boolean = true,
        ignoreEmails: Boolean = true,
        preserveHashtags: Boolean = false,
        preserveMentions: Boolean = false
    ): List<String> {
        if (text.isBlank()) return emptyList()

        var processed = text

        // 1. Remove URLs if configured
        if (ignoreUrls) {
            processed = URL_PATTERN.matcher(processed).replaceAll(" ")
        }

        // 2. Remove Emails if configured
        if (ignoreEmails) {
            processed = EMAIL_PATTERN.matcher(processed).replaceAll(" ")
        }

        // 3. Normalize dash separators (em-dash, en-dash) to spaces
        processed = DASH_SEPARATORS.matcher(processed).replaceAll(" ")

        // 4. Normalize Unicode apostrophes and hyphens
        processed = UNICODE_APOSTROPHES.matcher(processed).replaceAll("'")
        processed = UNICODE_HYPHENS.matcher(processed).replaceAll("-")

        // 5. Handle Apostrophe Mode pre-processing
        when (apostropheMode) {
            ApostropheMode.SPLIT -> {
                processed = processed.replace('\'', ' ')
            }
            ApostropheMode.REMOVE -> {
                processed = processed.replace("'", "")
            }
            ApostropheMode.PRESERVE -> {
                // Internal apostrophes preserved via matcher regex
            }
        }

        // 6. Handle Hyphen Mode pre-processing
        when (hyphenMode) {
            HyphenMode.SPLIT -> {
                processed = processed.replace('-', ' ')
            }
            HyphenMode.REMOVE -> {
                processed = processed.replace("-", "")
            }
            HyphenMode.KEEP -> {
                // Internal hyphens preserved via matcher regex
            }
        }

        // 7. Select appropriate word pattern based on modes
        val pattern = when {
            apostropheMode == ApostropheMode.PRESERVE && hyphenMode == HyphenMode.KEEP -> WORD_PATTERN_BOTH
            apostropheMode == ApostropheMode.PRESERVE -> WORD_PATTERN_APOSTROPHE
            hyphenMode == HyphenMode.KEEP -> WORD_PATTERN_HYPHEN
            else -> WORD_PATTERN_SIMPLE
        }

        val matcher = pattern.matcher(processed)
        val tokens = ArrayList<String>()

        while (matcher.find()) {
            var token = matcher.group()

            // Normalize case
            if (!caseSensitive) {
                token = token.lowercase(Locale.ROOT)
            }

            // Number handling
            if (!includeNumbers && isPureNumber(token)) {
                continue
            }

            if (token.isNotEmpty()) {
                tokens.add(token)
            }
        }

        return tokens
    }

    /**
     * Determines whether a token is composed purely of digits.
     */
    fun isPureNumber(token: String): Boolean {
        if (token.isEmpty()) return false
        var hasDigit = false
        for (i in 0 until token.length) {
            val codePoint = token.codePointAt(i)
            if (Character.isDigit(codePoint)) {
                hasDigit = true
            } else if (token[i] != '-' && token[i] != '.') {
                // Contains a non-digit, non-numeric punctuation -> not a pure number
                return false
            }
        }
        return hasDigit
    }
}
