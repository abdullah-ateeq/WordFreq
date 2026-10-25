package com.example.wordfreq.data.model

data class WordFrequencyResult(
    val rawTokensCount: Int = 0,
    val validWordsCount: Int = 0,
    val filteredTokensCount: Int = 0,
    val uniqueWordsCount: Int = 0,
    val results: List<WordFrequency> = emptyList(),
    val mostFrequentWords: List<Pair<String, Int>> = emptyList(),
    val leastFrequentWordsCount: Int = 0,
    val averageFrequency: Double = 0.0,
    val shortestWord: String? = null,
    val longestWord: String? = null,
    val averageWordLength: Double = 0.0,
    val frequencyDistribution: Map<String, Int> = emptyMap(),
    val normalizedTokensPreview: List<String> = emptyList()
)
