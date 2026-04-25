@file:Suppress("MagicNumber")

package com.example.notes.feature.notes.app

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notes.feature.notes.domain.NoteColorKeys
import com.example.notes.feature.notes.presentation.NoteEditorUiState
import com.example.notes.feature.notes.presentation.NoteListItemUiModel
import com.example.notes.feature.notes.presentation.NotesListUiState

@Composable
@Suppress("LongMethod", "LongParameterList")
fun NotesEditorScreen(
    uiState: NotesListUiState,
    editorState: NoteEditorUiState,
    lastMessage: String?,
    copy: NotesUiCopy,
    onBackClick: () -> Unit,
    onMoreClick: () -> Unit,
    onSaveClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onColorSelected: (String) -> Unit,
    onNoteSelected: (NoteListItemUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = noteEditorPaletteFor(editorState.selectedColorKey)
    Surface(
        modifier = modifier.fillMaxSize(),
        color = palette.background,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
        ) {
            item {
                NotesEditorTopBar(
                    copy = copy,
                    canSave = editorState.title.isNotBlank() && editorState.hasUnsavedChanges,
                    onBackClick = onBackClick,
                    onMoreClick = onMoreClick,
                    onSaveClick = onSaveClick,
                )
                Spacer(modifier = Modifier.height(18.dp))
                ColorSwatchRow(
                    selectedColorKey = editorState.selectedColorKey,
                    copy = copy,
                    onColorSelected = onColorSelected,
                )
                Spacer(modifier = Modifier.height(28.dp))
                EditorTitleField(
                    value = editorState.title,
                    placeholder = copy.titlePlaceholder,
                    textColor = palette.content,
                    onValueChange = onTitleChange,
                )
                Spacer(modifier = Modifier.height(24.dp))
                EditorBodyField(
                    value = editorState.content,
                    placeholder = copy.bodyPlaceholder,
                    textColor = palette.content,
                    onValueChange = onContentChange,
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = editorState.updatedAt.toEditedLabel(copy),
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.content.copy(alpha = 0.56f),
                )
                if (lastMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = lastMessage,
                        style = MaterialTheme.typography.labelMedium,
                        color = palette.content.copy(alpha = 0.72f),
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = copy.recentNotesTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.content,
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            if (uiState.notes.isEmpty()) {
                item {
                    Text(
                        text = copy.emptyRecentNotes,
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.content.copy(alpha = 0.58f),
                    )
                }
            } else {
                items(
                    items = uiState.notes,
                    key = { note -> note.id },
                ) { note ->
                    RecentNoteCard(
                        note = note,
                        copy = copy,
                        onClick = { onNoteSelected(note) },
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun NotesEditorTopBar(
    copy: NotesUiCopy,
    canSave: Boolean,
    onBackClick: () -> Unit,
    onMoreClick: () -> Unit,
    onSaveClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(onClick = onBackClick) {
            Text(text = copy.backAction)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(
                enabled = canSave,
                onClick = onSaveClick,
            ) {
                Text(text = copy.saveAction)
            }
            Spacer(modifier = Modifier.width(4.dp))
            TextButton(onClick = onMoreClick) {
                Text(text = copy.moreAction)
            }
        }
    }
}

@Composable
private fun ColorSwatchRow(
    selectedColorKey: String,
    copy: NotesUiCopy,
    onColorSelected: (String) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        noteEditorPalettes.forEach { palette ->
            val isSelected = palette.key == selectedColorKey
            Box(
                modifier =
                    Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(palette.background)
                        .border(
                            border =
                                BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color =
                                        if (isSelected) {
                                            palette.content.copy(alpha = 0.42f)
                                        } else {
                                            Color.White.copy(alpha = 0.72f)
                                        },
                                ),
                            shape = CircleShape,
                        ).clickable { onColorSelected(palette.key) }
                        .semantics {
                            contentDescription =
                                if (isSelected) {
                                    "${copy.colorSwatchLabel} ${palette.key} ${copy.colorSelectedSuffix}"
                                } else {
                                    "${copy.colorSwatchLabel} ${palette.key}"
                                }
                        },
            )
        }
    }
}

@Composable
private fun EditorTitleField(
    value: String,
    placeholder: String,
    textColor: Color,
    onValueChange: (String) -> Unit,
) {
    EditorTextField(
        value = value,
        placeholder = placeholder,
        textStyle =
            MaterialTheme.typography.headlineLarge.copy(
                color = textColor,
                fontWeight = FontWeight.ExtraBold,
            ),
        minHeight = 64.dp,
        onValueChange = onValueChange,
    )
}

@Composable
private fun EditorBodyField(
    value: String,
    placeholder: String,
    textColor: Color,
    onValueChange: (String) -> Unit,
) {
    EditorTextField(
        value = value,
        placeholder = placeholder,
        textStyle =
            MaterialTheme.typography.bodyLarge.copy(
                color = textColor,
            ),
        minHeight = 260.dp,
        onValueChange = onValueChange,
    )
}

@Composable
private fun EditorTextField(
    value: String,
    placeholder: String,
    textStyle: TextStyle,
    minHeight: androidx.compose.ui.unit.Dp,
    onValueChange: (String) -> Unit,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(min = minHeight),
        textStyle = textStyle,
        cursorBrush = SolidColor(textStyle.color),
        decorationBox = { innerTextField ->
            Box(modifier = Modifier.fillMaxWidth()) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = textStyle,
                        color = textStyle.color.copy(alpha = 0.36f),
                    )
                }
                innerTextField()
            }
        },
    )
}

@Composable
private fun RecentNoteCard(
    note: NoteListItemUiModel,
    copy: NotesUiCopy,
    onClick: () -> Unit,
) {
    val palette = noteEditorPaletteFor(note.colorKey)
    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onClick),
        color = palette.card,
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = note.title.ifBlank { copy.untitledFallback },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = palette.content,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = note.contentPreview.ifBlank { note.updatedAt.toEditedLabel(copy) },
                style = MaterialTheme.typography.bodySmall,
                color = palette.content.copy(alpha = 0.68f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private fun Long?.toEditedLabel(copy: NotesUiCopy): String =
    if (this == null) {
        copy.unsavedTimestamp
    } else {
        "${copy.editedPrefix} #$this"
    }

private data class NoteEditorPalette(
    val key: String,
    val background: Color,
    val card: Color,
    val content: Color,
)

private val noteEditorPalettes =
    listOf(
        NoteEditorPalette(
            key = NoteColorKeys.WHITE,
            background = Color(0xFFFFFBFF),
            card = Color(0xFFFFFFFF),
            content = Color(0xFF2C2830),
        ),
        NoteEditorPalette(
            key = NoteColorKeys.CREAM,
            background = Color(0xFFFFF2D7),
            card = Color(0xFFFFF8E8),
            content = Color(0xFF3B3021),
        ),
        NoteEditorPalette(
            key = NoteColorKeys.SKY,
            background = Color(0xFFE6F3FF),
            card = Color(0xFFF3FAFF),
            content = Color(0xFF243241),
        ),
        NoteEditorPalette(
            key = NoteColorKeys.MINT,
            background = Color(0xFFE3F7EA),
            card = Color(0xFFF1FBF4),
            content = Color(0xFF26382C),
        ),
        NoteEditorPalette(
            key = NoteColorKeys.LAVENDER,
            background = Color(0xFFF1E5F7),
            card = Color(0xFFF9F2FC),
            content = Color(0xFF30263B),
        ),
        NoteEditorPalette(
            key = NoteColorKeys.BLUSH,
            background = Color(0xFFFFE8EB),
            card = Color(0xFFFFF4F5),
            content = Color(0xFF3D2830),
        ),
    )

private fun noteEditorPaletteFor(colorKey: String): NoteEditorPalette =
    noteEditorPalettes.firstOrNull { palette -> palette.key == colorKey }
        ?: noteEditorPalettes.first { palette -> palette.key == NoteColorKeys.LAVENDER }

@Preview
@Composable
private fun NotesEditorScreenPreview() {
    MaterialTheme {
        NotesEditorScreen(
            uiState = NotesListUiState(),
            editorState =
                NoteEditorUiState(
                    title = "Project Ideas",
                    content =
                        listOf(
                            "1. Mobile note app with soft pop design",
                            "2. AI text summarizer",
                            "3. Personal finance tracker",
                        ).joinToString(separator = "\n"),
                    selectedColorKey = NoteColorKeys.LAVENDER,
                    updatedAt = 1L,
                ),
            lastMessage = null,
            copy = NotesUiCopy.English,
            onBackClick = {},
            onMoreClick = {},
            onSaveClick = {},
            onTitleChange = {},
            onContentChange = {},
            onColorSelected = {},
            onNoteSelected = {},
        )
    }
}
