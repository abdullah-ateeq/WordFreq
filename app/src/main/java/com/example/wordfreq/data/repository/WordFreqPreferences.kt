package com.example.wordfreq.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.wordfreq.data.model.ApostropheMode
import com.example.wordfreq.data.model.HyphenMode
import com.example.wordfreq.data.model.ResultLimit
import com.example.wordfreq.data.model.SortMode
import com.example.wordfreq.data.model.ThemeMode
import com.example.wordfreq.data.model.WordFrequencySettings

class WordFreqPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "word_freq_preferences"

        private const val KEY_THEME = "theme_mode"
        private const val KEY_DYNAMIC_COLOR = "dynamic_color"

        private const val KEY_CASE_SENSITIVE = "case_sensitive"
        private const val KEY_INCLUDE_NUMBERS = "include_numbers"
        private const val KEY_APOSTROPHE_MODE = "apostrophe_mode"
        private const val KEY_HYPHEN_MODE = "hyphen_mode"
        private const val KEY_STOP_WORDS_ENABLED = "stop_words_enabled"
        private const val KEY_CUSTOM_STOP_WORDS = "custom_stop_words"
        private const val KEY_MIN_WORD_LENGTH = "min_word_length"
        private const val KEY_MAX_WORD_LENGTH = "max_word_length"

        private const val KEY_SORT_MODE = "sort_mode"
        private const val KEY_SORT_DESCENDING = "sort_descending"
        private const val KEY_RESULT_LIMIT = "result_limit"
        private const val KEY_TOP_WORDS_COUNT = "top_words_count"

        private const val KEY_SHOW_FREQ_CHART = "show_freq_chart"
        private const val KEY_SHOW_DISTRIBUTION = "show_distribution"
        private const val KEY_SHOW_WORD_LENGTH = "show_word_length"
        private const val KEY_SHOW_PREVIEW = "show_preview"

        private const val KEY_LIVE_ANALYSIS = "live_analysis"
        private const val KEY_HAPTIC_FEEDBACK = "haptic_feedback"
        private const val KEY_ANIMATIONS_ENABLED = "animations_enabled"
        private const val KEY_CONFIRM_DELETE = "confirm_delete"
        private const val KEY_RESTORE_LAST_TEXT = "restore_last_text"
        private const val KEY_LAST_TEXT = "last_text"
    }

    var themeMode: ThemeMode
        get() {
            val name = prefs.getString(KEY_THEME, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
            return try { ThemeMode.valueOf(name) } catch (e: Exception) { ThemeMode.SYSTEM }
        }
        set(value) = prefs.edit().putString(KEY_THEME, value.name).apply()

    var dynamicColor: Boolean
        get() = prefs.getBoolean(KEY_DYNAMIC_COLOR, true)
        set(value) = prefs.edit().putBoolean(KEY_DYNAMIC_COLOR, value).apply()

    var caseSensitive: Boolean
        get() = prefs.getBoolean(KEY_CASE_SENSITIVE, false)
        set(value) = prefs.edit().putBoolean(KEY_CASE_SENSITIVE, value).apply()

    var includeNumbers: Boolean
        get() = prefs.getBoolean(KEY_INCLUDE_NUMBERS, true)
        set(value) = prefs.edit().putBoolean(KEY_INCLUDE_NUMBERS, value).apply()

    var apostropheMode: ApostropheMode
        get() {
            val name = prefs.getString(KEY_APOSTROPHE_MODE, ApostropheMode.PRESERVE.name) ?: ApostropheMode.PRESERVE.name
            return try { ApostropheMode.valueOf(name) } catch (e: Exception) { ApostropheMode.PRESERVE }
        }
        set(value) = prefs.edit().putString(KEY_APOSTROPHE_MODE, value.name).apply()

    var hyphenMode: HyphenMode
        get() {
            val name = prefs.getString(KEY_HYPHEN_MODE, HyphenMode.KEEP.name) ?: HyphenMode.KEEP.name
            return try { HyphenMode.valueOf(name) } catch (e: Exception) { HyphenMode.KEEP }
        }
        set(value) = prefs.edit().putString(KEY_HYPHEN_MODE, value.name).apply()

    var stopWordsEnabled: Boolean
        get() = prefs.getBoolean(KEY_STOP_WORDS_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_STOP_WORDS_ENABLED, value).apply()

    var customStopWords: Set<String>
        get() = prefs.getStringSet(KEY_CUSTOM_STOP_WORDS, emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet(KEY_CUSTOM_STOP_WORDS, value).apply()

    var minimumWordLength: Int
        get() = prefs.getInt(KEY_MIN_WORD_LENGTH, 1)
        set(value) = prefs.edit().putInt(KEY_MIN_WORD_LENGTH, value).apply()

    var maximumWordLength: Int?
        get() {
            val v = prefs.getInt(KEY_MAX_WORD_LENGTH, -1)
            return if (v > 0) v else null
        }
        set(value) = prefs.edit().putInt(KEY_MAX_WORD_LENGTH, value ?: -1).apply()

    var sortMode: SortMode
        get() {
            val name = prefs.getString(KEY_SORT_MODE, SortMode.FREQUENCY.name) ?: SortMode.FREQUENCY.name
            return try { SortMode.valueOf(name) } catch (e: Exception) { SortMode.FREQUENCY }
        }
        set(value) = prefs.edit().putString(KEY_SORT_MODE, value.name).apply()

    var sortDescending: Boolean
        get() = prefs.getBoolean(KEY_SORT_DESCENDING, true)
        set(value) = prefs.edit().putBoolean(KEY_SORT_DESCENDING, value).apply()

    var resultLimit: ResultLimit
        get() {
            val name = prefs.getString(KEY_RESULT_LIMIT, ResultLimit.ALL.name) ?: ResultLimit.ALL.name
            return try { ResultLimit.valueOf(name) } catch (e: Exception) { ResultLimit.ALL }
        }
        set(value) = prefs.edit().putString(KEY_RESULT_LIMIT, value.name).apply()

    var topWordsCount: Int
        get() = prefs.getInt(KEY_TOP_WORDS_COUNT, 5)
        set(value) = prefs.edit().putInt(KEY_TOP_WORDS_COUNT, value).apply()

    var showFrequencyChart: Boolean
        get() = prefs.getBoolean(KEY_SHOW_FREQ_CHART, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_FREQ_CHART, value).apply()

    var showDistribution: Boolean
        get() = prefs.getBoolean(KEY_SHOW_DISTRIBUTION, false)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_DISTRIBUTION, value).apply()

    var showWordLength: Boolean
        get() = prefs.getBoolean(KEY_SHOW_WORD_LENGTH, false)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_WORD_LENGTH, value).apply()

    var showPreview: Boolean
        get() = prefs.getBoolean(KEY_SHOW_PREVIEW, false)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_PREVIEW, value).apply()

    var liveAnalysis: Boolean
        get() = prefs.getBoolean(KEY_LIVE_ANALYSIS, true)
        set(value) = prefs.edit().putBoolean(KEY_LIVE_ANALYSIS, value).apply()

    var hapticFeedback: Boolean
        get() = prefs.getBoolean(KEY_HAPTIC_FEEDBACK, true)
        set(value) = prefs.edit().putBoolean(KEY_HAPTIC_FEEDBACK, value).apply()

    var animationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_ANIMATIONS_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_ANIMATIONS_ENABLED, value).apply()

    var confirmDelete: Boolean
        get() = prefs.getBoolean(KEY_CONFIRM_DELETE, true)
        set(value) = prefs.edit().putBoolean(KEY_CONFIRM_DELETE, value).apply()

    var restoreLastText: Boolean
        get() = prefs.getBoolean(KEY_RESTORE_LAST_TEXT, true)
        set(value) = prefs.edit().putBoolean(KEY_RESTORE_LAST_TEXT, value).apply()

    var lastText: String
        get() = prefs.getString(KEY_LAST_TEXT, "") ?: ""
        set(value) = prefs.edit().putString(KEY_LAST_TEXT, value).apply()

    fun getAnalysisSettings(): WordFrequencySettings {
        return WordFrequencySettings(
            caseSensitive = caseSensitive,
            includeNumbers = includeNumbers,
            apostropheMode = apostropheMode,
            hyphenMode = hyphenMode,
            stopWordsEnabled = stopWordsEnabled,
            customStopWords = customStopWords,
            minimumWordLength = minimumWordLength,
            maximumWordLength = maximumWordLength
        )
    }

    fun resetSettingsToDefaults() {
        prefs.edit()
            .putString(KEY_THEME, ThemeMode.SYSTEM.name)
            .putBoolean(KEY_DYNAMIC_COLOR, true)
            .putBoolean(KEY_CASE_SENSITIVE, false)
            .putBoolean(KEY_INCLUDE_NUMBERS, true)
            .putString(KEY_APOSTROPHE_MODE, ApostropheMode.PRESERVE.name)
            .putString(KEY_HYPHEN_MODE, HyphenMode.KEEP.name)
            .putBoolean(KEY_STOP_WORDS_ENABLED, false)
            .putStringSet(KEY_CUSTOM_STOP_WORDS, emptySet())
            .putInt(KEY_MIN_WORD_LENGTH, 1)
            .putInt(KEY_MAX_WORD_LENGTH, -1)
            .putString(KEY_SORT_MODE, SortMode.FREQUENCY.name)
            .putBoolean(KEY_SORT_DESCENDING, true)
            .putString(KEY_RESULT_LIMIT, ResultLimit.ALL.name)
            .putInt(KEY_TOP_WORDS_COUNT, 5)
            .putBoolean(KEY_SHOW_FREQ_CHART, true)
            .putBoolean(KEY_SHOW_DISTRIBUTION, false)
            .putBoolean(KEY_SHOW_WORD_LENGTH, false)
            .putBoolean(KEY_SHOW_PREVIEW, false)
            .putBoolean(KEY_LIVE_ANALYSIS, true)
            .putBoolean(KEY_HAPTIC_FEEDBACK, true)
            .putBoolean(KEY_ANIMATIONS_ENABLED, true)
            .putBoolean(KEY_CONFIRM_DELETE, true)
            .putBoolean(KEY_RESTORE_LAST_TEXT, true)
            .apply()
    }
}
