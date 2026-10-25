package com.example.wordfreq.data.model

enum class ApostropheMode {
    PRESERVE,
    SPLIT,
    REMOVE
}

enum class HyphenMode {
    KEEP,
    SPLIT,
    REMOVE
}

enum class SortMode {
    FREQUENCY,
    ALPHABETICAL,
    PERCENTAGE,
    WORD_LENGTH
}

enum class ResultLimit(val limit: Int?) {
    ALL(null),
    TOP_10(10),
    TOP_25(25),
    TOP_50(50),
    TOP_100(100);

    val label: String
        get() = when (this) {
            ALL -> "All"
            TOP_10 -> "Top 10"
            TOP_25 -> "Top 25"
            TOP_50 -> "Top 50"
            TOP_100 -> "Top 100"
        }
}

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}
