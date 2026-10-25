package com.example.wordfreq.data.model

data class WordFreqUiState(
    val text: String = "",
    val caseSensitive: Boolean = false,
    val includeNumbers: Boolean = true,
    val apostropheMode: ApostropheMode = ApostropheMode.PRESERVE,
    val hyphenMode: HyphenMode = HyphenMode.KEEP,
    val stopWordsEnabled: Boolean = false,
    val customStopWords: Set<String> = emptySet(),
    val minimumWordLength: Int = 1,
    val maximumWordLength: Int? = null,
    val sortMode: SortMode = SortMode.FREQUENCY,
    val sortDescending: Boolean = true,
    val resultLimit: ResultLimit = ResultLimit.ALL,
    val searchQuery: String = "",
    val results: List<WordFrequency> = emptyList(), // Displayed/filtered results
    val allResults: List<WordFrequency> = emptyList(), // Complete analyzed results
    val totalTokens: Int = 0,
    val validWords: Int = 0,
    val filteredWords: Int = 0,
    val uniqueWords: Int = 0,
    val mostFrequentWords: List<Pair<String, Int>> = emptyList(),
    val leastFrequentWordsCount: Int = 0,
    val averageFrequency: Double = 0.0,
    val shortestWord: String? = null,
    val longestWord: String? = null,
    val averageWordLength: Double = 0.0,
    val frequencyDistribution: Map<String, Int> = emptyMap(),
    val normalizedPreview: List<String> = emptyList(),
    val isAnalyzing: Boolean = false,
    val liveAnalysis: Boolean = true,
    val showFrequencyChart: Boolean = true,
    val showDistribution: Boolean = false,
    val showWordLength: Boolean = false,
    val showPreview: Boolean = false,
    val topWordsCount: Int = 5,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = true,
    val hapticFeedback: Boolean = true,
    val animationsEnabled: Boolean = true,
    val confirmDelete: Boolean = true,
    val restoreLastText: Boolean = true,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val infoMessage: String? = null
) {
    // Lightweight live character, words, lines stats on input text
    val liveCharCount: Int
        get() = text.length

    val liveCharNoSpacesCount: Int
        get() = text.count { !it.isWhitespace() }

    val liveWordCount: Int
        get() {
            if (text.isBlank()) return 0
            var count = 0
            var inWord = false
            for (i in 0 until text.length) {
                if (text[i].isWhitespace()) {
                    inWord = false
                } else if (!inWord) {
                    inWord = true
                    count++
                }
            }
            return count
        }

    val liveLineCount: Int
        get() {
            if (text.isEmpty()) return 0
            return text.count { it == '\n' } + 1
        }
}
