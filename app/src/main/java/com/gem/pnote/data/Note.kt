package com.gem.pnote.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class Note(
    val id: String = UUID.randomUUID().toString(),
    val header: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false
) {
    val formattedDate: String
        get() {
            val formatter = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
            return formatter.format(Date(updatedAt))
        }

    val wordCount: Int
        get() {
            val fullText = "$header $content".trim()
            if (fullText.isEmpty()) return 0
            return fullText.split("\\s+".toRegex()).count { it.isNotBlank() }
        }

    val characterCount: Int
        get() = header.length + content.length
}
