package com.example.wordfreq.data.model

data class WordFrequency(
    val word: String,
    val frequency: Int,
    val percentage: Double,
    val rank: Int
) {
    val formattedPercentage: String
        get() = String.format(java.util.Locale.US, "%.2f%%", percentage)

    val wordLength: Int
        get() = word.codePointCount(0, word.length)
}
