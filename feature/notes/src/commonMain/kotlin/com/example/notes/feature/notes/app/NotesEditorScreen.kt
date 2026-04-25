@file:Suppress("MagicNumber", "TooManyFunctions")

package com.example.notes.feature.notes.app

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notes.feature.notes.domain.NoteColorKeys
import com.example.notes.feature.notes.domain.NoteFilter
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
    onSaveClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onColorSelected: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onFilterSelected: (NoteFilter) -> Unit,
    onEditorCompletedChange: (Boolean) -> Unit,
    onRequestDelete: (String) -> Unit,
    onDismissDelete: () -> Unit,
    onConfirmDelete: () -> Unit,
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
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        ) {
            item {
                NotesEditorTopBar(
                    copy = copy,
                    activeNoteId = editorState.activeNoteId,
                    isCompleted = editorState.isCompleted,
                    canSave = editorState.title.isNotBlank() && editorState.hasUnsavedChanges,
                    onBackClick = onBackClick,
                    onSaveClick = onSaveClick,
                    onEditorCompletedChange = onEditorCompletedChange,
                    onRequestDelete = onRequestDelete,
                )
                Spacer(modifier = Modifier.height(16.dp))
                ColorSwatchRow(
                    selectedColorKey = editorState.selectedColorKey,
                    copy = copy,
                    onColorSelected = onColorSelected,
                )
                Spacer(modifier = Modifier.height(26.dp))
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
                Spacer(modifier = Modifier.height(16.dp))
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
                SearchField(
                    value = uiState.searchQuery,
                    copy = copy,
                    palette = palette,
                    onValueChange = onSearchQueryChange,
                )
                Spacer(modifier = Modifier.height(12.dp))
                FilterRow(
                    selectedFilter = uiState.filter,
                    copy = copy,
                    onFilterSelected = onFilterSelected,
                )
                Spacer(modifier = Modifier.height(20.dp))
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
                        isSelected = note.id == editorState.activeNoteId,
                        onClick = { onNoteSelected(note) },
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }

    if (uiState.pendingDeleteNoteId != null) {
        DeleteNoteDialog(
            copy = copy,
            onDismissDelete = onDismissDelete,
            onConfirmDelete = onConfirmDelete,
        )
    }
}

@Composable
@Suppress("LongMethod", "LongParameterList")
private fun NotesEditorTopBar(
    copy: NotesUiCopy,
    activeNoteId: String?,
    isCompleted: Boolean,
    canSave: Boolean,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onEditorCompletedChange: (Boolean) -> Unit,
    onRequestDelete: (String) -> Unit,
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    val actionColor = MaterialTheme.colorScheme.onSurface
    val disabledActionColor = actionColor.copy(alpha = 0.34f)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = onBackClick,
            modifier =
                Modifier
                    .size(44.dp)
                    .semantics { contentDescription = copy.backAction },
        ) {
            BackIcon(color = actionColor)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                enabled = canSave,
                onClick = onSaveClick,
                modifier =
                    Modifier
                        .size(44.dp)
                        .semantics { contentDescription = copy.saveAction },
            ) {
                SaveIcon(color = if (canSave) actionColor else disabledActionColor)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Box {
                IconButton(
                    onClick = { isMenuExpanded = true },
                    modifier =
                        Modifier
                            .size(44.dp)
                            .semantics { contentDescription = copy.moreAction },
                ) {
                    MoreIcon(color = actionColor)
                }
                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false },
                ) {
                    DropdownMenuItem(
                        text = { Text(text = copy.newNoteAction) },
                        onClick = {
                            isMenuExpanded = false
                            onBackClick()
                        },
                    )
                    if (activeNoteId != null) {
                        DropdownMenuItem(
                            text =
                                {
                                    Text(
                                        text =
                                            if (isCompleted) {
                                                copy.markActiveAction
                                            } else {
                                                copy.markCompleteAction
                                            },
                                    )
                                },
                            onClick = {
                                isMenuExpanded = false
                                onEditorCompletedChange(!isCompleted)
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(text = copy.deleteNoteAction) },
                            onClick = {
                                isMenuExpanded = false
                                onRequestDelete(activeNoteId)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BackIcon(color: Color) {
    Canvas(modifier = Modifier.size(22.dp)) {
        val strokeWidth = size.minDimension * ICON_STROKE_WIDTH_RATIO
        drawLine(
            color = color,
            start = Offset(size.width * 0.7f, size.height * 0.22f),
            end = Offset(size.width * 0.32f, size.height * 0.5f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.32f, size.height * 0.5f),
            end = Offset(size.width * 0.7f, size.height * 0.78f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.34f, size.height * 0.5f),
            end = Offset(size.width * 0.82f, size.height * 0.5f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun SaveIcon(color: Color) {
    Canvas(modifier = Modifier.size(22.dp)) {
        val strokeWidth = size.minDimension * ICON_STROKE_WIDTH_RATIO
        val inset = size.minDimension * 0.18f
        drawRect(
            color = color,
            topLeft = Offset(inset, inset),
            size =
                Size(
                    width = size.width - (inset * 2),
                    height = size.height - (inset * 2),
                ),
            style = Stroke(width = strokeWidth),
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.36f, size.height * 0.22f),
            end = Offset(size.width * 0.64f, size.height * 0.22f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = color,
            start = Offset(size.width * 0.34f, size.height * 0.68f),
            end = Offset(size.width * 0.66f, size.height * 0.68f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun MoreIcon(color: Color) {
    Canvas(modifier = Modifier.size(22.dp)) {
        val radius = size.minDimension * 0.07f
        drawCircle(color = color, radius = radius, center = Offset(size.width * 0.5f, size.height * 0.26f))
        drawCircle(color = color, radius = radius, center = Offset(size.width * 0.5f, size.height * 0.5f))
        drawCircle(color = color, radius = radius, center = Offset(size.width * 0.5f, size.height * 0.74f))
    }
}

@Composable
private fun SearchField(
    value: String,
    copy: NotesUiCopy,
    palette: NoteEditorPalette,
    onValueChange: (String) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = palette.card,
        shape = RoundedCornerShape(8.dp),
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
            textStyle =
                MaterialTheme.typography.bodyMedium.copy(
                    color = palette.content,
                ),
            cursorBrush = SolidColor(palette.content),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (value.isEmpty()) {
                        Text(
                            text = copy.searchPlaceholder,
                            style = MaterialTheme.typography.bodyMedium,
                            color = palette.content.copy(alpha = 0.46f),
                        )
                    }
                    innerTextField()
                }
            },
        )
    }
}

@Composable
private fun FilterRow(
    selectedFilter: NoteFilter,
    copy: NotesUiCopy,
    onFilterSelected: (NoteFilter) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NoteFilter.entries.forEach { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text(text = filter.label(copy)) },
            )
        }
    }
}

@Composable
private fun DeleteNoteDialog(
    copy: NotesUiCopy,
    onDismissDelete: () -> Unit,
    onConfirmDelete: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissDelete,
        title = { Text(text = copy.deleteDialogTitle) },
        text = { Text(text = copy.deleteDialogMessage) },
        confirmButton = {
            TextButton(onClick = onConfirmDelete) {
                Text(text = copy.confirmDeleteAction)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissDelete) {
                Text(text = copy.cancelAction)
            }
        },
    )
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
        minHeight = 360.dp,
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
@Suppress("LongMethod")
private fun RecentNoteCard(
    note: NoteListItemUiModel,
    copy: NotesUiCopy,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val palette = noteEditorPaletteFor(note.colorKey)
    val titleDecoration =
        if (note.isCompleted) {
            TextDecoration.LineThrough
        } else {
            TextDecoration.None
        }
    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .border(
                    border =
                        BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color =
                                if (isSelected) {
                                    palette.content.copy(alpha = 0.3f)
                                } else {
                                    Color.White.copy(alpha = 0.48f)
                                },
                        ),
                    shape = RoundedCornerShape(8.dp),
                ).clickable(onClick = onClick),
        color = palette.card,
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = note.title.ifBlank { copy.untitledFallback },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = palette.content,
                textDecoration = titleDecoration,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = note.contentPreview.ifBlank { note.updatedAt.toEditedLabel(copy) },
                style = MaterialTheme.typography.bodySmall,
                color = palette.content.copy(alpha = 0.68f),
                textDecoration = titleDecoration,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text =
                    if (note.isCompleted) {
                        copy.completedStatusLabel
                    } else {
                        copy.activeStatusLabel
                    },
                style = MaterialTheme.typography.labelSmall,
                color = palette.content.copy(alpha = 0.54f),
            )
        }
    }
}

private fun NoteFilter.label(copy: NotesUiCopy): String =
    when (this) {
        NoteFilter.ALL -> copy.allFilterLabel
        NoteFilter.ACTIVE -> copy.activeFilterLabel
        NoteFilter.COMPLETED -> copy.completedFilterLabel
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

private const val ICON_STROKE_WIDTH_RATIO = 0.08f

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
                    updatedAt = 1_777_071_491_000L,
                ),
            lastMessage = null,
            copy = NotesUiCopy.English,
            onBackClick = {},
            onSaveClick = {},
            onTitleChange = {},
            onContentChange = {},
            onColorSelected = {},
            onSearchQueryChange = {},
            onFilterSelected = {},
            onEditorCompletedChange = {},
            onRequestDelete = {},
            onDismissDelete = {},
            onConfirmDelete = {},
            onNoteSelected = {},
        )
    }
}
