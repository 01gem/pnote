package com.gem.pnote.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gem.pnote.data.Note
import com.gem.pnote.data.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(
    private val repository: NoteRepository
) : ViewModel() {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredNotes: StateFlow<List<Note>> = combine(_notes, _searchQuery) { allNotes, query ->
        if (query.isBlank()) {
            allNotes
        } else {
            allNotes.filter {
                it.header.contains(query, ignoreCase = true) ||
                        it.content.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentEditingNote = MutableStateFlow<Note?>(null)
    val currentEditingNote: StateFlow<Note?> = _currentEditingNote.asStateFlow()

    private val _isEditorOpen = MutableStateFlow(false)
    val isEditorOpen: StateFlow<Boolean> = _isEditorOpen.asStateFlow()

    private val _isCsvDialogOpen = MutableStateFlow(false)
    val isCsvDialogOpen: StateFlow<Boolean> = _isCsvDialogOpen.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    init {
        loadNotes()
    }

    fun loadNotes() {
        viewModelScope.launch {
            _notes.value = repository.getNotes()
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun openNewNote() {
        _currentEditingNote.value = null
        _isEditorOpen.value = true
    }

    fun openNote(note: Note) {
        _currentEditingNote.value = note
        _isEditorOpen.value = true
    }

    fun closeEditor() {
        _isEditorOpen.value = false
        _currentEditingNote.value = null
    }

    fun saveNote(header: String, content: String) {
        val trimmedHeader = header.trim()
        val trimmedContent = content.trim()

        if (trimmedHeader.isEmpty() && trimmedContent.isEmpty()) {
            closeEditor()
            return
        }

        val effectiveHeader = if (trimmedHeader.isNotEmpty()) {
            trimmedHeader
        } else {
            // If header was empty, use first few words of content as header
            val firstLine = trimmedContent.lineSequence().firstOrNull() ?: "Untitled Note"
            if (firstLine.length > 40) firstLine.take(40) + "..." else firstLine
        }

        val now = System.currentTimeMillis()
        val existing = _currentEditingNote.value
        val noteToSave = if (existing != null) {
            existing.copy(
                header = effectiveHeader,
                content = trimmedContent,
                updatedAt = now
            )
        } else {
            Note(
                header = effectiveHeader,
                content = trimmedContent,
                createdAt = now,
                updatedAt = now
            )
        }

        viewModelScope.launch {
            repository.saveNote(noteToSave)
            loadNotes()
            closeEditor()
            showSnackbar(if (existing != null) "Note updated" else "Note created")
        }
    }

    fun deleteNote(id: String) {
        viewModelScope.launch {
            repository.deleteNote(id)
            loadNotes()
            if (_isEditorOpen.value && _currentEditingNote.value?.id == id) {
                closeEditor()
            }
            showSnackbar("Note deleted")
        }
    }

    fun togglePin(note: Note) {
        viewModelScope.launch {
            repository.togglePin(note)
            loadNotes()
            showSnackbar(if (!note.isPinned) "Note pinned to top" else "Note unpinned")
        }
    }

    fun openCsvDialog() {
        _isCsvDialogOpen.value = true
    }

    fun closeCsvDialog() {
        _isCsvDialogOpen.value = false
    }

    fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }
}
