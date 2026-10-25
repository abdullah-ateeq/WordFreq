package com.example.wordfreq.data.model

data class WordFrequencySettings(
    val caseSensitive: Boolean = false,
    val includeNumbers: Boolean = true,
    val apostropheMode: ApostropheMode = ApostropheMode.PRESERVE,
    val hyphenMode: HyphenMode = HyphenMode.KEEP,
    val stopWordsEnabled: Boolean = false,
    val customStopWords: Set<String> = emptySet(),
    val minimumWordLength: Int = 1,
    val maximumWordLength: Int? = null,
    val ignoreUrls: Boolean = true,
    val ignoreEmails: Boolean = true,
    val preserveHashtags: Boolean = false,
    val preserveMentions: Boolean = false
)
