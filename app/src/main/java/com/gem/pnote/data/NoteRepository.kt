package com.gem.pnote.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NoteRepository(context: Context) {
    private val dbHelper = NotesDatabaseHelper(context.applicationContext)

    suspend fun getNotes(): List<Note> = withContext(Dispatchers.IO) {
        dbHelper.getAllNotes()
    }

    suspend fun saveNote(note: Note) = withContext(Dispatchers.IO) {
        val existingNotes = dbHelper.getAllNotes()
        if (existingNotes.any { it.id == note.id }) {
            dbHelper.updateNote(note)
        } else {
            dbHelper.insertNote(note)
        }
    }

    suspend fun deleteNote(id: String) = withContext(Dispatchers.IO) {
        dbHelper.deleteNote(id)
    }

    suspend fun togglePin(note: Note) = withContext(Dispatchers.IO) {
        val updated = note.copy(isPinned = !note.isPinned)
        dbHelper.updateNote(updated)
    }
}
