package com.example.wordfreq

import com.example.wordfreq.data.model.WordFrequency
import com.example.wordfreq.export.CsvExporter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CsvExporterTest {

    @Test
    fun testCsvEscaping() {
        assertEquals("normal", CsvExporter.escapeCsvField("normal"))
        assertEquals("\"hello,world\"", CsvExporter.escapeCsvField("hello,world"))
        assertEquals("\"say \"\"hi\"\"\"", CsvExporter.escapeCsvField("say \"hi\""))
        assertEquals("\"line1\nline2\"", CsvExporter.escapeCsvField("line1\nline2"))
    }

    @Test
    fun testGenerateCsv() {
        val list = listOf(
            WordFrequency(word = "the", frequency = 24, percentage = 11.01, rank = 1),
            WordFrequency(word = "café", frequency = 10, percentage = 4.59, rank = 2)
        )
        val csv = CsvExporter.generateCsv(list)
        val lines = csv.trim().lines()

        assertEquals("Rank,Word,Frequency,Percentage,Word Length", lines[0])
        assertEquals("1,the,24,11.01,3", lines[1])
        assertEquals("2,café,10,4.59,4", lines[2])
    }
}
