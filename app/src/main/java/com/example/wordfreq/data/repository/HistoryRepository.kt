package com.example.wordfreq.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.wordfreq.data.model.WordFreqHistory
import org.json.JSONArray
import org.json.JSONObject

class HistoryRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "word_freq_history_prefs"
        private const val KEY_HISTORY_LIST = "history_list_json"
        const val MAX_HISTORY_ITEMS = 10
    }

    @Synchronized
    fun getHistory(): List<WordFreqHistory> {
        val jsonStr = prefs.getString(KEY_HISTORY_LIST, null) ?: return emptyList()
        return try {
            val jsonArray = JSONArray(jsonStr)
            val list = mutableListOf<WordFreqHistory>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val maxLen = if (obj.has("maximumWordLength") && !obj.isNull("maximumWordLength")) {
                    obj.getInt("maximumWordLength")
                } else null

                list.add(
                    WordFreqHistory(
                        id = obj.getString("id"),
                        text = obj.getString("text"),
                        analyzedAt = obj.getLong("analyzedAt"),
                        caseSensitive = obj.getBoolean("caseSensitive"),
                        includeNumbers = obj.getBoolean("includeNumbers"),
                        apostropheMode = obj.getString("apostropheMode"),
                        hyphenMode = obj.getString("hyphenMode"),
                        stopWordsEnabled = obj.getBoolean("stopWordsEnabled"),
                        minimumWordLength = obj.getInt("minimumWordLength"),
                        maximumWordLength = maxLen,
                        wordCount = obj.optInt("wordCount", 0),
                        uniqueCount = obj.optInt("uniqueCount", 0),
                        topWord = obj.optString("topWord", "")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    @Synchronized
    fun saveHistory(entry: WordFreqHistory): Boolean {
        val currentList = getHistory().toMutableList()

        // Check for consecutive duplicate:
        // "If the exact same text and analysis configuration are saved consecutively, do not create an unnecessary duplicate."
        if (currentList.isNotEmpty()) {
            val latest = currentList.first()
            val isDuplicate = latest.text == entry.text &&
                    latest.caseSensitive == entry.caseSensitive &&
                    latest.includeNumbers == entry.includeNumbers &&
                    latest.apostropheMode == entry.apostropheMode &&
                    latest.hyphenMode == entry.hyphenMode &&
                    latest.stopWordsEnabled == entry.stopWordsEnabled &&
                    latest.minimumWordLength == entry.minimumWordLength &&
                    latest.maximumWordLength == entry.maximumWordLength

            if (isDuplicate) {
                return false
            }
        }

        // Insert at beginning
        currentList.add(0, entry)

        // Enforce maximum 10 entries (remove oldest)
        while (currentList.size > MAX_HISTORY_ITEMS) {
            currentList.removeAt(currentList.lastIndex)
        }

        saveList(currentList)
        return true
    }

    @Synchronized
    fun deleteHistory(id: String) {
        val currentList = getHistory().toMutableList()
        currentList.removeAll { it.id == id }
        saveList(currentList)
    }

    @Synchronized
    fun clearHistory() {
        prefs.edit().remove(KEY_HISTORY_LIST).apply()
    }

    private fun saveList(list: List<WordFreqHistory>) {
        val jsonArray = JSONArray()
        for (item in list) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("text", item.text)
            obj.put("analyzedAt", item.analyzedAt)
            obj.put("caseSensitive", item.caseSensitive)
            obj.put("includeNumbers", item.includeNumbers)
            obj.put("apostropheMode", item.apostropheMode)
            obj.put("hyphenMode", item.hyphenMode)
            obj.put("stopWordsEnabled", item.stopWordsEnabled)
            obj.put("minimumWordLength", item.minimumWordLength)
            if (item.maximumWordLength != null) {
                obj.put("maximumWordLength", item.maximumWordLength)
            } else {
                obj.put("maximumWordLength", JSONObject.NULL)
            }
            obj.put("wordCount", item.wordCount)
            obj.put("uniqueCount", item.uniqueCount)
            obj.put("topWord", item.topWord)
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_HISTORY_LIST, jsonArray.toString()).apply()
    }
}
