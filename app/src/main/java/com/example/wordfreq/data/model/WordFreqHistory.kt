package com.example.wordfreq.data.model

data class WordFreqHistory(
    val id: String,
    val text: String,
    val analyzedAt: Long,
    val caseSensitive: Boolean,
    val includeNumbers: Boolean,
    val apostropheMode: String,
    val hyphenMode: String,
    val stopWordsEnabled: Boolean,
    val minimumWordLength: Int,
    val maximumWordLength: Int?,
    val wordCount: Int = 0,
    val uniqueCount: Int = 0,
    val topWord: String = ""
)
