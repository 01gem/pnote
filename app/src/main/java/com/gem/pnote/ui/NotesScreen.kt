package com.gem.pnote.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gem.pnote.ui.components.CsvExportDialog
import com.gem.pnote.ui.components.NoteCard
import com.gem.pnote.ui.components.NoteEditorScreen
import com.gem.pnote.ui.theme.CardBorder
import com.gem.pnote.ui.theme.CardDark
import com.gem.pnote.ui.theme.CardElevated
import com.gem.pnote.ui.theme.ObsidianDark
import com.gem.pnote.ui.theme.OrangeContainer
import com.gem.pnote.ui.theme.OrangeContainerBorder
import com.gem.pnote.ui.theme.OrangeLight
import com.gem.pnote.ui.theme.OrangePrimary
import com.gem.pnote.ui.theme.OrangeVibrant
import com.gem.pnote.ui.theme.PNoteIcons
import com.gem.pnote.ui.theme.PitchBlack
import com.gem.pnote.ui.theme.TextMuted
import com.gem.pnote.ui.theme.TextPrimary
import com.gem.pnote.ui.theme.TextSecondary

@Composable
fun NotesScreen(
    viewModel: NotesViewModel,
    modifier: Modifier = Modifier
) {
    val notes by viewModel.notes.collectAsState()
    val filteredNotes by viewModel.filteredNotes.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isEditorOpen by viewModel.isEditorOpen.collectAsState()
    val currentEditingNote by viewModel.currentEditingNote.collectAsState()
    val isCsvDialogOpen by viewModel.isCsvDialogOpen.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    if (isEditorOpen) {
        NoteEditorScreen(
            note = currentEditingNote,
            onBack = { viewModel.closeEditor() },
            onSave = { header, content ->
                viewModel.saveNote(header, content)
            },
            onDelete = { id ->
                viewModel.deleteNote(id)
            }
        )
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = PitchBlack,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openNewNote() },
                containerColor = OrangePrimary,
                contentColor = PitchBlack,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.size(58.dp)
            ) {
                Icon(
                    imageVector = PNoteIcons.Add,
                    contentDescription = "New Note",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 18.dp)
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // TOP BAR: App Branding + Main Feature "Extract to CSV"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Branding with Orange Dot
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(OrangeVibrant, OrangePrimary)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "P",
                            color = PitchBlack,
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "PNote",
                                color = TextPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(OrangePrimary)
                            )
                        }
                        Text(
                            text = "Headers-first • CSV Export",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                // MAIN FEATURE BUTTON: Extract CSV
                Button(
                    onClick = {
                        if (notes.isEmpty()) {
                            viewModel.showSnackbar("No notes to extract to CSV!")
                        } else {
                            viewModel.openCsvDialog()
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangeContainer,
                        contentColor = OrangeLight
                    ),
                    border = BorderStroke(1.dp, OrangeContainerBorder),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "CSV",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        color = OrangePrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Extract CSV",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = OrangeLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // MODERN SEARCH BAR: Rounded corners with dark styling
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardDark)
                    .border(BorderStroke(1.dp, CardBorder), RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = PNoteIcons.Search,
                        contentDescription = "Search",
                        tint = OrangePrimary.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    BasicTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        textStyle = TextStyle(
                            color = TextPrimary,
                            fontSize = 14.sp
                        ),
                        cursorBrush = SolidColor(OrangePrimary),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Search headers or content...",
                                    color = TextMuted,
                                    fontSize = 14.sp
                                )
                            }
                            innerTextField()
                        }
                    )

                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.onSearchQueryChange("") },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = PNoteIcons.Close,
                                contentDescription = "Clear search",
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section Info Row: Note count + Header indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "NOTES",
                        color = TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CardElevated)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${filteredNotes.size}",
                            color = OrangeLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = "Headers shown first",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // LIST OF NOTES: Headers first before clicking the note
            if (filteredNotes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(CardDark)
                                .border(BorderStroke(1.dp, OrangeContainerBorder), RoundedCornerShape(20.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "📝",
                                fontSize = 28.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (searchQuery.isNotEmpty()) "No matching notes" else "No notes yet",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (searchQuery.isNotEmpty())
                                "Try searching for a different keyword or clear the search."
                            else
                                "Tap '+' to create your first note with a prominent header.",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        if (searchQuery.isEmpty()) {
                            Spacer(modifier = Modifier.height(18.dp))
                            Button(
                                onClick = { viewModel.openNewNote() },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = OrangePrimary,
                                    contentColor = PitchBlack
                                )
                            ) {
                                Text(
                                    text = "Create Note",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = filteredNotes,
                        key = { it.id }
                    ) { note ->
                        NoteCard(
                            note = note,
                            onClick = { viewModel.openNote(note) },
                            onDelete = { viewModel.deleteNote(note.id) },
                            onTogglePin = { viewModel.togglePin(note) }
                        )
                    }
                }
            }
        }
    }

    // CSV Extraction Dialog
    if (isCsvDialogOpen) {
        CsvExportDialog(
            notes = notes,
            onDismiss = { viewModel.closeCsvDialog() },
            onExportSuccess = { message ->
                viewModel.showSnackbar(message)
            }
        )
    }
}
