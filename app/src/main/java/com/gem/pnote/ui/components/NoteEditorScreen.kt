package com.gem.pnote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gem.pnote.data.Note
import com.gem.pnote.ui.theme.CardBorder
import com.gem.pnote.ui.theme.CardElevated
import com.gem.pnote.ui.theme.DeleteRed
import com.gem.pnote.ui.theme.OrangeLight
import com.gem.pnote.ui.theme.OrangePrimary
import com.gem.pnote.ui.theme.PNoteIcons
import com.gem.pnote.ui.theme.PitchBlack
import com.gem.pnote.ui.theme.TextMuted
import com.gem.pnote.ui.theme.TextPrimary
import com.gem.pnote.ui.theme.TextSecondary

@Composable
fun NoteEditorScreen(
    note: Note?,
    onBack: () -> Unit,
    onSave: (header: String, content: String) -> Unit,
    onDelete: (id: String) -> Unit
) {
    var header by remember(note) { mutableStateOf(note?.header ?: "") }
    var content by remember(note) { mutableStateOf(note?.content ?: "") }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(note) {
        if (note == null) {
            // Auto focus header for new notes
            try {
                focusRequester.requestFocus()
            } catch (_: Exception) {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PitchBlack)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top Navigation Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardElevated)
            ) {
                Icon(
                    imageVector = PNoteIcons.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (note != null) {
                    IconButton(
                        onClick = { onDelete(note.id) },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardElevated)
                    ) {
                        Icon(
                            imageVector = PNoteIcons.Delete,
                            contentDescription = "Delete Note",
                            tint = DeleteRed
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))
                }

                Button(
                    onClick = { onSave(header, content) },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangePrimary,
                        contentColor = PitchBlack
                    )
                ) {
                    Icon(
                        imageVector = PNoteIcons.Check,
                        contentDescription = "Save",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Save",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Editor Scrollable Area
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Label Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(CardElevated)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "HEADER",
                    color = OrangeLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // HEADER INPUT FIRST: Large, bold, prominent
            BasicTextField(
                value = header,
                onValueChange = { header = it },
                textStyle = TextStyle(
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 32.sp
                ),
                cursorBrush = SolidColor(OrangePrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                decorationBox = { innerTextField ->
                    if (header.isEmpty()) {
                        Text(
                            text = "Note Header / Title",
                            color = TextMuted,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    innerTextField()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(
                color = CardBorder,
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Content Body Field
            BasicTextField(
                value = content,
                onValueChange = { content = it },
                textStyle = TextStyle(
                    color = TextSecondary,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Normal
                ),
                cursorBrush = SolidColor(OrangePrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                decorationBox = { innerTextField ->
                    if (content.isEmpty()) {
                        Text(
                            text = "Write your note content here...\n\nAll notes can be extracted into CSV with one tap anytime.",
                            color = TextMuted,
                            fontSize = 16.sp,
                            lineHeight = 24.sp
                        )
                    }
                    innerTextField()
                }
            )
        }

        // Bottom Stats Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val words = "$header $content".trim().split("\\s+".toRegex()).count { it.isNotBlank() }
            val chars = header.length + content.length

            Text(
                text = "$words words • $chars characters",
                color = TextMuted,
                fontSize = 12.sp
            )

            if (note != null) {
                Text(
                    text = "Modified: ${note.formattedDate}",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }
    }
}
