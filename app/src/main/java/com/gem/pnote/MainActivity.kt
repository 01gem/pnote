package com.gem.pnote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.gem.pnote.data.NoteRepository
import com.gem.pnote.ui.NotesScreen
import com.gem.pnote.ui.NotesViewModel
import com.gem.pnote.ui.theme.PNoteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val repository = remember { NoteRepository(applicationContext) }
            val viewModel = remember { NotesViewModel(repository) }

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
