package com.example.wordfreq.export

import com.example.wordfreq.data.model.WordFrequency
import java.util.Locale

object CsvExporter {

    fun generateCsv(results: List<WordFrequency>): String {
        val sb = StringBuilder()
        sb.append("Rank,Word,Frequency,Percentage,Word Length\n")

        for (item in results) {
            val escapedWord = escapeCsvField(item.word)
            val formattedPercentage = String.format(Locale.US, "%.2f", item.percentage)
            sb.append("${item.rank},$escapedWord,${item.frequency},$formattedPercentage,${item.wordLength}\n")
        }

        return sb.toString()
    }

    fun escapeCsvField(field: String): String {
        val needsQuotes = field.contains(',') || field.contains('"') || field.contains('\n') || field.contains('\r')
        return if (needsQuotes) {
            "\"" + field.replace("\"", "\"\"") + "\""
        } else {
            field
        }
    }
}
