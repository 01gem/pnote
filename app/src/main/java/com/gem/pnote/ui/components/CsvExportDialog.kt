package com.gem.pnote.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.gem.pnote.data.CsvExporter
import com.gem.pnote.data.Note
import com.gem.pnote.ui.theme.CardBorder
import com.gem.pnote.ui.theme.CardElevated
import com.gem.pnote.ui.theme.ObsidianDark
import com.gem.pnote.ui.theme.OrangeContainer
import com.gem.pnote.ui.theme.OrangeContainerBorder
import com.gem.pnote.ui.theme.OrangeLight
import com.gem.pnote.ui.theme.OrangePrimary
import com.gem.pnote.ui.theme.PNoteIcons
import com.gem.pnote.ui.theme.PitchBlack
import com.gem.pnote.ui.theme.TextMuted
import com.gem.pnote.ui.theme.TextPrimary
import com.gem.pnote.ui.theme.TextSecondary

@Composable
fun CsvExportDialog(
    notes: List<Note>,
    onDismiss: () -> Unit,
    onExportSuccess: (String) -> Unit,
) {
    val context = LocalContext.current
    val csvString = remember(notes) { CsvExporter.generateCsv(notes) }
    val defaultFileName = remember { CsvExporter.getDefaultFileName() }

    // Android Storage Access Framework (SAF) launcher for saving CSV to device
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv"),
    ) { uri ->
        if (uri != null) {
            val success = CsvExporter.writeCsvToUri(context, uri, notes)
            if (success) {
                onExportSuccess("CSV exported successfully to device storage!")
                onDismiss()
            } else {
                onExportSuccess("Failed to export CSV file.")
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = ObsidianDark,
            ),
            border = BorderStroke(1.dp, OrangeContainerBorder),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
            ) {
                // Header with Orange Accent Badge and Close Icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(OrangeContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "CSV",
                                color = OrangeLight,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Extract to CSV",
                                color = TextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Standard RFC 4180 export",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = PNoteIcons.Close,
                            contentDescription = "Close",
                            tint = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatPill(
                        label = "Notes",
                        value = notes.size.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    StatPill(
                        label = "Total Words",
                        value = notes.sumOf { it.wordCount }.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    StatPill(
                        label = "File Size",
                        value = (csvString.toByteArray().size / 1024.0).let { String.format(java.util.Locale.US, "%.1f KB", it) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Preview section
                Text(
                    text = "CSV Preview",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Code-like preview box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(PitchBlack)
                        .padding(12.dp)
                ) {
                    val previewScrollState = rememberScrollState()
                    val previewHScrollState = rememberScrollState()

                    Text(
                        text = csvString.ifEmpty { "No notes to preview" },
                        color = OrangeLight.copy(alpha = 0.9f),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 16.sp,
                        modifier = Modifier
                            .verticalScroll(previewScrollState)
                            .horizontalScroll(previewHScrollState)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action: Save to Device (Primary SAF)
                Button(
                    onClick = {
                        createDocumentLauncher.launch(defaultFileName)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangePrimary,
                        contentColor = PitchBlack
                    )
                ) {
                    Text(
                        text = "Save CSV to Device Storage",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action: Share CSV via Intent
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val shareIntent = CsvExporter.createShareIntent(context, notes)
                            if (shareIntent != null) {
                                context.startActivity(
                                    android.content.Intent.createChooser(
                                        shareIntent,
                                        "Extract & Share CSV"
                                    )
                                )
                                onDismiss()
                            } else {
                                onExportSuccess("Unable to create shareable CSV file.")
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = OrangeLight
                        ),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Icon(
                            imageVector = PNoteIcons.Share,
                            contentDescription = "Share",
                            tint = OrangeLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Share File",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }

                    // Action: Copy CSV text to clipboard
                    OutlinedButton(
                        onClick = {
                            val clipboard =
                                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Notes CSV", csvString)
                            clipboard.setPrimaryClip(clip)
                            onExportSuccess("CSV text copied to clipboard!")
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextSecondary
                        ),
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Text(
                            text = "Copy CSV",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CardElevated)
            .padding(vertical = 10.dp, horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                color = OrangeLight,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
