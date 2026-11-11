package com.gem.pnote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gem.pnote.data.NoteRepository
import com.gem.pnote.ui.NotesScreen
import com.gem.pnote.ui.NotesViewModel
import com.gem.pnote.ui.theme.PNoteTheme

class MainActivity : ComponentActivity() {
    private val viewModel: NotesViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NotesViewModel(NoteRepository(applicationContext)) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isEditorOpen by viewModel.isEditorOpen.collectAsState()
            val isCsvDialogOpen by viewModel.isCsvDialogOpen.collectAsState()
            val searchQuery by viewModel.searchQuery.collectAsState()

            // Handle system back gesture
            BackHandler(enabled = isEditorOpen || isCsvDialogOpen || searchQuery.isNotEmpty()) {
                when {
                    isCsvDialogOpen -> viewModel.closeCsvDialog()
                    isEditorOpen -> viewModel.closeEditor()
                    searchQuery.isNotEmpty() -> viewModel.onSearchQueryChange("")
                }
            }

            PNoteTheme {
                NotesScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
