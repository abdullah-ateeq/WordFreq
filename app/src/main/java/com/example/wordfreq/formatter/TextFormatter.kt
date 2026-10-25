package com.example.wordfreq.formatter

import com.example.wordfreq.data.model.WordFrequency
import com.example.wordfreq.data.model.WordFreqUiState
import java.util.Locale

object TextFormatter {

    fun formatCopyResults(
        totalWords: Int,
        uniqueWords: Int,
        results: List<WordFrequency>,
        limit: Int? = null
    ): String {
        val list = if (limit != null && limit > 0) results.take(limit) else results
        val sb = StringBuilder()
        sb.append("WordFreq\n\n")
        sb.append("Total words: $totalWords\n")
        sb.append("Unique words: $uniqueWords\n\n")
        sb.append("Word Frequency\n\n")

        list.forEachIndexed { index, item ->
            val rank = index + 1
            val formattedPercentage = String.format(Locale.US, "%.2f%%", item.percentage)
            sb.append("$rank. ${item.word} — ${item.frequency} — $formattedPercentage\n")
        }

        return sb.toString().trimEnd()
    }

    fun formatShareResults(
        uiState: WordFreqUiState,
        topCount: Int = 5
    ): String {
        val sb = StringBuilder()
        sb.append("WordFreq\n\n")
        sb.append("Text statistics:\n")
        sb.append("Total words: ${uiState.validWords}\n")
        sb.append("Unique words: ${uiState.uniqueWords}\n")
        sb.append("Filtered: ${uiState.filteredWords}\n\n")

        if (uiState.mostFrequentWords.isNotEmpty()) {
            val mostFreqStr = uiState.mostFrequentWords.joinToString(", ") { "${it.first} — ${it.second}" }
            sb.append("Most frequent:\n$mostFreqStr\n\n")
        }

        sb.append("Top words:\n\n")
        val topList = uiState.allResults.take(topCount)
        topList.forEachIndexed { index, item ->
            val rank = index + 1
            val formattedPercentage = String.format(Locale.US, "%.2f%%", item.percentage)
            sb.append("$rank. ${item.word} — ${item.frequency} — $formattedPercentage\n")
        }

        return sb.toString().trimEnd()
    }
}
