package com.gem.pnote

import com.gem.pnote.data.CsvExporter
import com.gem.pnote.data.Note
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CsvExporterTest {

    @Test
    fun `generateCsv generates correct header row`() {
        val notes = emptyList<Note>()
        val csv = CsvExporter.generateCsv(notes)
        val lines = csv.split("\r\n")

        assertEquals("ID,Header,Content,Word Count,Created Date,Last Modified Date", lines[0])
    }

    @Test
    fun `generateCsv correctly formats standard note`() {
        val note = Note(
            id = "test-uuid-123",
            header = "Weekly Standup",
            content = "Discussing Q4 deliverables and sprint retro.",
            createdAt = 1700000000000L,
            updatedAt = 1700000000000L,
            isPinned = false
        )

        val csv = CsvExporter.generateCsv(listOf(note))
        val lines = csv.split("\r\n").filter { it.isNotBlank() }

        assertEquals(2, lines.size)
        assertTrue(lines[1].startsWith("test-uuid-123,Weekly Standup,Discussing Q4 deliverables and sprint retro.,8,"))
    }

    @Test
    fun `generateCsv properly escapes fields with commas and newlines`() {
        val note = Note(
            id = "test-2",
            header = "Apples, Bananas, and Oranges",
            content = "Line 1\nLine 2 with, commas and \"quoted\" words.",
            createdAt = 1700000000000L,
            updatedAt = 1700000000000L
        )

        val csv = CsvExporter.generateCsv(listOf(note))

        // Must enclose fields with commas in quotes
        assertTrue(csv.contains("\"Apples, Bananas, and Oranges\""))
        // Must escape internal double quotes as ""
        assertTrue(csv.contains("\"\"quoted\"\""))
    }

    @Test
    fun `note model calculates word and character counts accurately`() {
        val note = Note(
            header = "Quick Brown Fox",
            content = "Jumps over the lazy dog"
        )

        assertEquals(8, note.wordCount)
        assertEquals(38, note.characterCount)
    }

    @Test
    fun `getDefaultFileName returns valid csv extension`() {
        val fileName = CsvExporter.getDefaultFileName()
        assertTrue(fileName.startsWith("PNote_Export_"))
        assertTrue(fileName.endsWith(".csv"))
    }
}
