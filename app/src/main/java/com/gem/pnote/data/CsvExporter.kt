package com.gem.pnote.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CsvExporter {

    private val csvDateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    private val fileNameFormatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)

    /**
     * Converts a list of notes to standard RFC 4180 CSV string.
     */
    fun generateCsv(notes: List<Note>): String {
        val sb = StringBuilder()

        // CSV Header
        sb.append(escapeCsvField("ID"))
            .append(",")
            .append(escapeCsvField("Header"))
            .append(",")
            .append(escapeCsvField("Content"))
            .append(",")
            .append(escapeCsvField("Word Count"))
            .append(",")
            .append(escapeCsvField("Created Date"))
            .append(",")
            .append(escapeCsvField("Last Modified Date"))
            .append("\r\n")

        for (note in notes) {
            val createdStr = csvDateFormatter.format(Date(note.createdAt))
            val updatedStr = csvDateFormatter.format(Date(note.updatedAt))

            sb.append(escapeCsvField(note.id)).append(",")
                .append(escapeCsvField(note.header)).append(",")
                .append(escapeCsvField(note.content)).append(",")
                .append(escapeCsvField(note.wordCount.toString())).append(",")
                .append(escapeCsvField(createdStr)).append(",")
                .append(escapeCsvField(updatedStr))
                .append("\r\n")
        }

        return sb.toString()
    }

    /**
     * Escapes field according to RFC 4180 specifications:
     * - Fields with commas, quotes, or newlines are wrapped in double quotes.
     * - Double quotes inside fields are replaced with two double quotes ("").
     */
    private fun escapeCsvField(value: String): String {
        val containsSpecialChars = value.contains(',') ||
                value.contains('"') ||
                value.contains('\n') ||
                value.contains('\r')

        return if (containsSpecialChars) {
            "\"" + value.replace("\"", "\"\"") + "\""
        } else {
            value
        }
    }

    fun getDefaultFileName(): String {
        val timestamp = fileNameFormatter.format(Date())
        return "PNote_Export_$timestamp.csv"
    }

    /**
     * Writes CSV data directly to a Uri obtained from ActivityResultContracts.CreateDocument.
     */
    fun writeCsvToUri(context: Context, uri: Uri, notes: List<Note>): Boolean {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream, StandardCharsets.UTF_8).use { writer ->
                    // Write UTF-8 BOM so Excel opens it with proper characters out of the box
                    outputStream.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))
                    writer.write(generateCsv(notes))
                    writer.flush()
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Creates a shareable temporary CSV file and returns an ACTION_SEND Intent.
     */
    fun createShareIntent(context: Context, notes: List<Note>): Intent? {
        return try {
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) {
                exportDir.mkdirs()
            }

            val fileName = getDefaultFileName()
            val file = File(exportDir, fileName)

            FileOutputStream(file).use { fos ->
                // Write UTF-8 BOM
                fos.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))
                OutputStreamWriter(fos, StandardCharsets.UTF_8).use { writer ->
                    writer.write(generateCsv(notes))
                    writer.flush()
                }
            }

            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_SUBJECT, "PNote Export ($fileName)")
                putExtra(Intent.EXTRA_TEXT, "Exported ${notes.size} notes from PNote.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
