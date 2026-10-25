package com.example.wordfreq.analyzer

import com.example.wordfreq.data.model.SortMode
import com.example.wordfreq.data.model.WordFrequency
import com.example.wordfreq.data.model.WordFrequencyResult
import com.example.wordfreq.data.model.WordFrequencySettings
import com.example.wordfreq.tokenizer.WordTokenizer
import java.text.Collator
import java.util.Locale

object WordFrequencyAnalyzer {

    fun analyze(
        text: String,
        settings: WordFrequencySettings,
        sortMode: SortMode = SortMode.FREQUENCY,
        sortDescending: Boolean = true
    ): WordFrequencyResult {
        if (text.isBlank()) {
            return WordFrequencyResult()
        }

        // Step 1 & 2: Tokenize and Normalize
        // We obtain raw tokens (preserving all numbers initially to track raw vs filtered count)
        val rawTokens = WordTokenizer.tokenize(
            text = text,
            caseSensitive = settings.caseSensitive,
            includeNumbers = true,
            apostropheMode = settings.apostropheMode,
            hyphenMode = settings.hyphenMode,
            ignoreUrls = settings.ignoreUrls,
            ignoreEmails = settings.ignoreEmails,
            preserveHashtags = settings.preserveHashtags,
            preserveMentions = settings.preserveMentions
        )

        val rawTokensCount = rawTokens.size
        if (rawTokensCount == 0) {
            return WordFrequencyResult()
        }

        // Prepare custom stop words set for efficient lookup
        val normalizedCustomStopWords: Set<String> = if (settings.caseSensitive) {
            settings.customStopWords
        } else {
            settings.customStopWords.map { it.lowercase(Locale.ROOT) }.toSet()
        }

        // Step 3-6: Apply Filters in deterministic order
        val validWords = ArrayList<String>(rawTokensCount)

        for (token in rawTokens) {
            // Handle numbers
            if (!settings.includeNumbers && WordTokenizer.isPureNumber(token)) {
                continue
            }

            // Apply stop words
            if (settings.stopWordsEnabled) {
                val checkWord = if (settings.caseSensitive) token.lowercase(Locale.ROOT) else token
                if (StopWords.ENGLISH_STOP_WORDS.contains(checkWord)) {
                    continue
                }
            }

            // Apply custom exclusions
            if (normalizedCustomStopWords.isNotEmpty()) {
                val checkWord = if (settings.caseSensitive) token else token.lowercase(Locale.ROOT)
                if (normalizedCustomStopWords.contains(checkWord)) {
                    continue
                }
            }

            // Apply minimum and maximum word length (Unicode code point aware)
            val codePointLength = token.codePointCount(0, token.length)
            if (codePointLength < settings.minimumWordLength) {
                continue
            }
            if (settings.maximumWordLength != null && codePointLength > settings.maximumWordLength) {
                continue
            }

            validWords.add(token)
        }

        val validWordsCount = validWords.size
        val filteredTokensCount = rawTokensCount - validWordsCount

        if (validWordsCount == 0) {
            return WordFrequencyResult(
                rawTokensCount = rawTokensCount,
                validWordsCount = 0,
                filteredTokensCount = filteredTokensCount,
                uniqueWordsCount = 0
            )
        }

        // Step 7: Count frequencies
        val frequencyMap = HashMap<String, Int>(validWordsCount)
        for (word in validWords) {
            val count = frequencyMap[word] ?: 0
            frequencyMap[word] = count + 1
        }

        val uniqueWordsCount = frequencyMap.size

        // Step 8: Calculate percentages
        val unrankedList = frequencyMap.map { (word, freq) ->
            val percentage = (freq.toDouble() / validWordsCount.toDouble()) * 100.0
            WordFrequency(
                word = word,
                frequency = freq,
                percentage = percentage,
                rank = 0
            )
        }

        // Step 9: Sort results
        val sortedList = sortResults(unrankedList, sortMode, sortDescending)

        // Step 10: Assign ranks
        val rankedResults = sortedList.mapIndexed { index, item ->
            item.copy(rank = index + 1)
        }

        // Summary Statistics
        val maxFrequency = rankedResults.maxOfOrNull { it.frequency } ?: 0
        val mostFrequentWords = rankedResults
            .filter { it.frequency == maxFrequency }
            .map { it.word to it.frequency }
            .sortedBy { it.first }

        val leastFrequentWordsCount = rankedResults.count { it.frequency == 1 }

        val averageFrequency = validWordsCount.toDouble() / uniqueWordsCount.toDouble()

        // Shortest / Longest / Average word length
        var shortest: String? = null
        var longest: String? = null
        var totalLength = 0L

        for (token in validWords) {
            val len = token.codePointCount(0, token.length)
            totalLength += len
            if (shortest == null || len < shortest.codePointCount(0, shortest.length)) {
                shortest = token
            }
            if (longest == null || len > longest.codePointCount(0, longest.length)) {
                longest = token
            }
        }
        val averageWordLength = totalLength.toDouble() / validWordsCount.toDouble()

        // Frequency distribution groups
        val dist1 = frequencyMap.count { it.value == 1 }
        val dist2 = frequencyMap.count { it.value == 2 }
        val dist3 = frequencyMap.count { it.value == 3 }
        val dist4 = frequencyMap.count { it.value == 4 }
        val dist5Plus = frequencyMap.count { it.value >= 5 }

        val distribution = linkedMapOf(
            "Words appearing 1 time" to dist1,
            "Words appearing 2 times" to dist2,
            "Words appearing 3 times" to dist3,
            "Words appearing 4 times" to dist4,
            "Words appearing 5+ times" to dist5Plus
        )

        val preview = validWords.take(50)

        return WordFrequencyResult(
            rawTokensCount = rawTokensCount,
            validWordsCount = validWordsCount,
            filteredTokensCount = filteredTokensCount,
            uniqueWordsCount = uniqueWordsCount,
            results = rankedResults,
            mostFrequentWords = mostFrequentWords,
            leastFrequentWordsCount = leastFrequentWordsCount,
            averageFrequency = averageFrequency,
            shortestWord = shortest,
            longestWord = longest,
            averageWordLength = averageWordLength,
            frequencyDistribution = distribution,
            normalizedTokensPreview = preview
        )
    }

    fun sortResults(
        list: List<WordFrequency>,
        sortMode: SortMode,
        sortDescending: Boolean
    ): List<WordFrequency> {
        val collator = Collator.getInstance(Locale.getDefault())

        val comparator = when (sortMode) {
            SortMode.FREQUENCY -> {
                Comparator<WordFrequency> { a, b ->
                    val freqComp = if (sortDescending) {
                        b.frequency.compareTo(a.frequency)
                    } else {
                        a.frequency.compareTo(b.frequency)
                    }
                    if (freqComp != 0) freqComp else collator.compare(a.word, b.word)
                }
            }
            SortMode.ALPHABETICAL -> {
                Comparator<WordFrequency> { a, b ->
                    val alphaComp = if (sortDescending) {
                        collator.compare(b.word, a.word)
                    } else {
                        collator.compare(a.word, b.word)
                    }
                    if (alphaComp != 0) alphaComp else b.frequency.compareTo(a.frequency)
                }
            }
            SortMode.PERCENTAGE -> {
                Comparator<WordFrequency> { a, b ->
                    val percComp = if (sortDescending) {
                        b.percentage.compareTo(a.percentage)
                    } else {
                        a.percentage.compareTo(b.percentage)
                    }
                    if (percComp != 0) percComp else collator.compare(a.word, b.word)
                }
            }
            SortMode.WORD_LENGTH -> {
                Comparator<WordFrequency> { a, b ->
                    val lenComp = if (sortDescending) {
                        b.wordLength.compareTo(a.wordLength)
                    } else {
                        a.wordLength.compareTo(b.wordLength)
                    }
                    if (lenComp != 0) lenComp else collator.compare(a.word, b.word)
                }
            }
        }

        return list.sortedWith(comparator)
    }
}
