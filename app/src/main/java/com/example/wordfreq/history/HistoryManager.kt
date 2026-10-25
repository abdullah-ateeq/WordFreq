package com.example.wordfreq.history

import com.example.wordfreq.data.model.WordFreqHistory
import com.example.wordfreq.data.repository.HistoryRepository

class HistoryManager(private val repository: HistoryRepository) {

    fun getAllHistory(): List<WordFreqHistory> = repository.getHistory()

    fun saveAnalysis(entry: WordFreqHistory): Boolean = repository.saveHistory(entry)

    fun deleteAnalysis(id: String) = repository.deleteHistory(id)

    fun clearAll() = repository.clearHistory()
}
