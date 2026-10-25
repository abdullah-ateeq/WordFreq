package com.example.wordfreq.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wordfreq.analyzer.WordFrequencyAnalyzer
import com.example.wordfreq.data.model.ApostropheMode
import com.example.wordfreq.data.model.HyphenMode
import com.example.wordfreq.data.model.ResultLimit
import com.example.wordfreq.data.model.SortMode
import com.example.wordfreq.data.model.ThemeMode
import com.example.wordfreq.data.model.WordFreqHistory
import com.example.wordfreq.data.model.WordFreqUiState
import com.example.wordfreq.data.model.WordFrequency
import com.example.wordfreq.data.model.WordFrequencySettings
import com.example.wordfreq.data.repository.HistoryRepository
import com.example.wordfreq.data.repository.WordFreqPreferences
import com.example.wordfreq.export.CsvExporter
import com.example.wordfreq.formatter.TextFormatter
import com.example.wordfreq.history.HistoryManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class WordFreqViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = WordFreqPreferences(application)
    private val historyRepository = HistoryRepository(application)
    private val historyManager = HistoryManager(historyRepository)

    private val _uiState = MutableStateFlow(WordFreqUiState())
    val uiState: StateFlow<WordFreqUiState> = _uiState.asStateFlow()

    private val _historyList = MutableStateFlow<List<WordFreqHistory>>(emptyList())
    val historyList: StateFlow<List<WordFreqHistory>> = _historyList.asStateFlow()

    private var analysisJob: Job? = null
    private var debounceJob: Job? = null

    init {
        loadSettings()
        loadHistory()
        if (preferences.restoreLastText && preferences.lastText.isNotBlank()) {
            _uiState.update { it.copy(text = preferences.lastText) }
            analyzeText(isLive = false)
        }
    }

    private fun loadSettings() {
        _uiState.update {
            it.copy(
                caseSensitive = preferences.caseSensitive,
                includeNumbers = preferences.includeNumbers,
                apostropheMode = preferences.apostropheMode,
                hyphenMode = preferences.hyphenMode,
                stopWordsEnabled = preferences.stopWordsEnabled,
                customStopWords = preferences.customStopWords,
                minimumWordLength = preferences.minimumWordLength,
                maximumWordLength = preferences.maximumWordLength,
                sortMode = preferences.sortMode,
                sortDescending = preferences.sortDescending,
                resultLimit = preferences.resultLimit,
                topWordsCount = preferences.topWordsCount,
                showFrequencyChart = preferences.showFrequencyChart,
                showDistribution = preferences.showDistribution,
                showWordLength = preferences.showWordLength,
                showPreview = preferences.showPreview,
                liveAnalysis = preferences.liveAnalysis,
                themeMode = preferences.themeMode,
                dynamicColor = preferences.dynamicColor,
                hapticFeedback = preferences.hapticFeedback,
                animationsEnabled = preferences.animationsEnabled,
                confirmDelete = preferences.confirmDelete,
                restoreLastText = preferences.restoreLastText
            )
        }
    }

    fun loadHistory() {
        _historyList.value = historyManager.getAllHistory()
    }

    fun onTextChanged(newText: String) {
        _uiState.update { it.copy(text = newText, errorMessage = null, successMessage = null) }
        preferences.lastText = newText

        if (newText.isBlank()) {
            clearResultsOnly()
            return
        }

        if (_uiState.value.liveAnalysis) {
            debounceJob?.cancel()
            debounceJob = viewModelScope.launch {
                delay(300) // 250-400ms debounce
                analyzeText(isLive = true)
            }
        }
    }

    fun onAnalyzeClicked() {
        val currentText = _uiState.value.text
        if (currentText.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Enter some text to analyze.") }
            return
        }
        analyzeText(isLive = false)
    }

    fun analyzeText(isLive: Boolean = false) {
        val currentText = _uiState.value.text
        if (currentText.isBlank()) {
            clearResultsOnly()
            return
        }

        analysisJob?.cancel()
        analysisJob = viewModelScope.launch {
            _uiState.update { it.copy(isAnalyzing = true, errorMessage = null) }

            val settings = WordFrequencySettings(
                caseSensitive = _uiState.value.caseSensitive,
                includeNumbers = _uiState.value.includeNumbers,
                apostropheMode = _uiState.value.apostropheMode,
                hyphenMode = _uiState.value.hyphenMode,
                stopWordsEnabled = _uiState.value.stopWordsEnabled,
                customStopWords = _uiState.value.customStopWords,
                minimumWordLength = _uiState.value.minimumWordLength,
                maximumWordLength = _uiState.value.maximumWordLength
            )

            val sortMode = _uiState.value.sortMode
            val sortDescending = _uiState.value.sortDescending

            val result = withContext(Dispatchers.Default) {
                WordFrequencyAnalyzer.analyze(
                    text = currentText,
                    settings = settings,
                    sortMode = sortMode,
                    sortDescending = sortDescending
                )
            }

            _uiState.update { state ->
                val allRankedResults = result.results
                val displayedResults = filterAndLimitResults(
                    allRankedResults,
                    state.searchQuery,
                    state.resultLimit
                )

                state.copy(
                    isAnalyzing = false,
                    totalTokens = result.rawTokensCount,
                    validWords = result.validWordsCount,
                    filteredWords = result.filteredTokensCount,
                    uniqueWords = result.uniqueWordsCount,
                    allResults = allRankedResults,
                    results = displayedResults,
                    mostFrequentWords = result.mostFrequentWords,
                    leastFrequentWordsCount = result.leastFrequentWordsCount,
                    averageFrequency = result.averageFrequency,
                    shortestWord = result.shortestWord,
                    longestWord = result.longestWord,
                    averageWordLength = result.averageWordLength,
                    frequencyDistribution = result.frequencyDistribution,
                    normalizedPreview = result.normalizedTokensPreview,
                    errorMessage = if (result.validWordsCount == 0 && currentText.isNotBlank()) {
                        "No words matched the active filter criteria."
                    } else null
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { state ->
            val displayed = filterAndLimitResults(state.allResults, query, state.resultLimit)
            state.copy(searchQuery = query, results = displayed)
        }
    }

    fun onSortModeChanged(mode: SortMode) {
        preferences.sortMode = mode
        _uiState.update { state ->
            val sortedAll = WordFrequencyAnalyzer.sortResults(state.allResults, mode, state.sortDescending)
            val ranked = sortedAll.mapIndexed { i, item -> item.copy(rank = i + 1) }
            val displayed = filterAndLimitResults(ranked, state.searchQuery, state.resultLimit)
            state.copy(sortMode = mode, allResults = ranked, results = displayed)
        }
    }

    fun onSortDirectionToggled() {
        val newDescending = !_uiState.value.sortDescending
        preferences.sortDescending = newDescending
        _uiState.update { state ->
            val sortedAll = WordFrequencyAnalyzer.sortResults(state.allResults, state.sortMode, newDescending)
            val ranked = sortedAll.mapIndexed { i, item -> item.copy(rank = i + 1) }
            val displayed = filterAndLimitResults(ranked, state.searchQuery, state.resultLimit)
            state.copy(sortDescending = newDescending, allResults = ranked, results = displayed)
        }
    }

    fun onResultLimitChanged(limit: ResultLimit) {
        preferences.resultLimit = limit
        _uiState.update { state ->
            val displayed = filterAndLimitResults(state.allResults, state.searchQuery, limit)
            state.copy(resultLimit = limit, results = displayed)
        }
    }

    private fun filterAndLimitResults(
        allResults: List<WordFrequency>,
        searchQuery: String,
        resultLimit: ResultLimit
    ): List<WordFrequency> {
        var filtered = allResults
        if (searchQuery.isNotBlank()) {
            val q = searchQuery.trim().lowercase()
            filtered = allResults.filter { it.word.lowercase().contains(q) }
        }
        return if (resultLimit.limit != null) {
            filtered.take(resultLimit.limit)
        } else {
            filtered
        }
    }

    fun clearAllText() {
        _uiState.update { it.copy(text = "", errorMessage = null, successMessage = null) }
        preferences.lastText = ""
        clearResultsOnly()
    }

    private fun clearResultsOnly() {
        analysisJob?.cancel()
        _uiState.update {
            it.copy(
                isAnalyzing = false,
                totalTokens = 0,
                validWords = 0,
                filteredWords = 0,
                uniqueWords = 0,
                allResults = emptyList(),
                results = emptyList(),
                mostFrequentWords = emptyList(),
                leastFrequentWordsCount = 0,
                averageFrequency = 0.0,
                shortestWord = null,
                longestWord = null,
                averageWordLength = 0.0,
                frequencyDistribution = emptyMap(),
                normalizedPreview = emptyList()
            )
        }
    }

    // Settings actions
    fun setCaseSensitive(value: Boolean) {
        preferences.caseSensitive = value
        _uiState.update { it.copy(caseSensitive = value) }
        if (_uiState.value.text.isNotBlank()) analyzeText()
    }

    fun setIncludeNumbers(value: Boolean) {
        preferences.includeNumbers = value
        _uiState.update { it.copy(includeNumbers = value) }
        if (_uiState.value.text.isNotBlank()) analyzeText()
    }

    fun setApostropheMode(mode: ApostropheMode) {
        preferences.apostropheMode = mode
        _uiState.update { it.copy(apostropheMode = mode) }
        if (_uiState.value.text.isNotBlank()) analyzeText()
    }

    fun setHyphenMode(mode: HyphenMode) {
        preferences.hyphenMode = mode
        _uiState.update { it.copy(hyphenMode = mode) }
        if (_uiState.value.text.isNotBlank()) analyzeText()
    }

    fun setStopWordsEnabled(value: Boolean) {
        preferences.stopWordsEnabled = value
        _uiState.update { it.copy(stopWordsEnabled = value) }
        if (_uiState.value.text.isNotBlank()) analyzeText()
    }

    fun addCustomStopWord(word: String) {
        val trimmed = word.trim()
        if (trimmed.isEmpty()) return
        val updated = preferences.customStopWords.toMutableSet()
        updated.add(trimmed)
        preferences.customStopWords = updated
        _uiState.update { it.copy(customStopWords = updated) }
        if (_uiState.value.text.isNotBlank()) analyzeText()
    }

    fun removeCustomStopWord(word: String) {
        val updated = preferences.customStopWords.toMutableSet()
        updated.remove(word)
        preferences.customStopWords = updated
        _uiState.update { it.copy(customStopWords = updated) }
        if (_uiState.value.text.isNotBlank()) analyzeText()
    }

    fun setMinimumWordLength(length: Int) {
        val valid = length.coerceAtLeast(1)
        preferences.minimumWordLength = valid
        _uiState.update { it.copy(minimumWordLength = valid) }
        if (_uiState.value.text.isNotBlank()) analyzeText()
    }

    fun setMaximumWordLength(length: Int?) {
        preferences.maximumWordLength = length
        _uiState.update { it.copy(maximumWordLength = length) }
        if (_uiState.value.text.isNotBlank()) analyzeText()
    }

    fun setTopWordsCount(count: Int) {
        preferences.topWordsCount = count
        _uiState.update { it.copy(topWordsCount = count) }
    }

    fun setShowFrequencyChart(value: Boolean) {
        preferences.showFrequencyChart = value
        _uiState.update { it.copy(showFrequencyChart = value) }
    }

    fun setShowDistribution(value: Boolean) {
        preferences.showDistribution = value
        _uiState.update { it.copy(showDistribution = value) }
    }

    fun setShowWordLength(value: Boolean) {
        preferences.showWordLength = value
        _uiState.update { it.copy(showWordLength = value) }
    }

    fun setShowPreview(value: Boolean) {
        preferences.showPreview = value
        _uiState.update { it.copy(showPreview = value) }
    }

    fun setLiveAnalysis(value: Boolean) {
        preferences.liveAnalysis = value
        _uiState.update { it.copy(liveAnalysis = value) }
    }

    fun setThemeMode(mode: ThemeMode) {
        preferences.themeMode = mode
        _uiState.update { it.copy(themeMode = mode) }
    }

    fun setDynamicColor(value: Boolean) {
        preferences.dynamicColor = value
        _uiState.update { it.copy(dynamicColor = value) }
    }

    fun setHapticFeedback(value: Boolean) {
        preferences.hapticFeedback = value
        _uiState.update { it.copy(hapticFeedback = value) }
    }

    fun setAnimationsEnabled(value: Boolean) {
        preferences.animationsEnabled = value
        _uiState.update { it.copy(animationsEnabled = value) }
    }

    fun setConfirmDelete(value: Boolean) {
        preferences.confirmDelete = value
        _uiState.update { it.copy(confirmDelete = value) }
    }

    fun setRestoreLastText(value: Boolean) {
        preferences.restoreLastText = value
        _uiState.update { it.copy(restoreLastText = value) }
    }

    fun resetSettings() {
        preferences.resetSettingsToDefaults()
        loadSettings()
        if (_uiState.value.text.isNotBlank()) {
            analyzeText()
        }
        _uiState.update { it.copy(successMessage = "Settings restored to defaults.") }
    }

    // History actions
    fun saveCurrentAnalysis(): Boolean {
        val state = _uiState.value
        if (state.text.isBlank() || state.validWords == 0) {
            _uiState.update { it.copy(errorMessage = "Enter and analyze text before saving.") }
            return false
        }

        val topWordDesc = if (state.mostFrequentWords.isNotEmpty()) {
            "${state.mostFrequentWords.first().first} — ${state.mostFrequentWords.first().second}"
        } else ""

        val historyEntry = WordFreqHistory(
            id = UUID.randomUUID().toString(),
            text = state.text,
            analyzedAt = System.currentTimeMillis(),
            caseSensitive = state.caseSensitive,
            includeNumbers = state.includeNumbers,
            apostropheMode = state.apostropheMode.name,
            hyphenMode = state.hyphenMode.name,
            stopWordsEnabled = state.stopWordsEnabled,
            minimumWordLength = state.minimumWordLength,
            maximumWordLength = state.maximumWordLength,
            wordCount = state.validWords,
            uniqueCount = state.uniqueWords,
            topWord = topWordDesc
        )

        val saved = historyManager.saveAnalysis(historyEntry)
        if (saved) {
            loadHistory()
            _uiState.update { it.copy(successMessage = "Analysis saved to history.") }
        } else {
            _uiState.update { it.copy(infoMessage = "Analysis already saved in history.") }
        }
        return saved
    }

    fun restoreHistory(entry: WordFreqHistory) {
        // Restore text
        preferences.lastText = entry.text
        // Restore settings
        val apostrophe = try { ApostropheMode.valueOf(entry.apostropheMode) } catch (e: Exception) { ApostropheMode.PRESERVE }
        val hyphen = try { HyphenMode.valueOf(entry.hyphenMode) } catch (e: Exception) { HyphenMode.KEEP }

        preferences.caseSensitive = entry.caseSensitive
        preferences.includeNumbers = entry.includeNumbers
        preferences.apostropheMode = apostrophe
        preferences.hyphenMode = hyphen
        preferences.stopWordsEnabled = entry.stopWordsEnabled
        preferences.minimumWordLength = entry.minimumWordLength
        preferences.maximumWordLength = entry.maximumWordLength

        _uiState.update {
            it.copy(
                text = entry.text,
                caseSensitive = entry.caseSensitive,
                includeNumbers = entry.includeNumbers,
                apostropheMode = apostrophe,
                hyphenMode = hyphen,
                stopWordsEnabled = entry.stopWordsEnabled,
                minimumWordLength = entry.minimumWordLength,
                maximumWordLength = entry.maximumWordLength,
                successMessage = "Analysis restored from history."
            )
        }

        // Regenerate frequency results
        analyzeText(isLive = false)
    }

    fun deleteHistoryEntry(id: String) {
        historyManager.deleteAnalysis(id)
        loadHistory()
        _uiState.update { it.copy(successMessage = "Analysis deleted.") }
    }

    fun clearAllHistory() {
        historyManager.clearAll()
        loadHistory()
        _uiState.update { it.copy(successMessage = "History cleared.") }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null, infoMessage = null) }
    }

    // Export & Sharing
    fun getCopyText(all: Boolean): String {
        val state = _uiState.value
        val list = if (all) state.allResults else state.results
        return TextFormatter.formatCopyResults(
            totalWords = state.validWords,
            uniqueWords = state.uniqueWords,
            results = list
        )
    }

    fun getShareText(): String {
        return TextFormatter.formatShareResults(_uiState.value, _uiState.value.topWordsCount)
    }

    fun getCsvContent(): String {
        return CsvExporter.generateCsv(_uiState.value.allResults)
    }
}
