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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.example.notes.core.designsystem.NotesColors
import com.example.notes.core.designsystem.NotesDesignSystem
import com.example.notes.core.designsystem.NotesNotePalette
import com.example.notes.core.designsystem.NotesTheme
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
    val colors = NotesTheme.colors
    val spacing = NotesTheme.spacing
    val typography = NotesTheme.typography
    val palette = colors.notePaletteFor(editorState.selectedColorKey)
    Surface(
        modifier = modifier.fillMaxSize(),
        color = palette.background,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding =
                PaddingValues(
                    horizontal = spacing.screenHorizontal,
                    vertical = spacing.screenVertical,
                ),
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
                Spacer(modifier = Modifier.height(spacing.md))
                ColorSwatchRow(
                    selectedColorKey = editorState.selectedColorKey,
                    copy = copy,
                    onColorSelected = onColorSelected,
                )
                Spacer(modifier = Modifier.height(spacing.lg + spacing.xxs))
                EditorTitleField(
                    value = editorState.title,
                    placeholder = copy.titlePlaceholder,
                    textColor = palette.content,
                    onValueChange = onTitleChange,
                )
                Spacer(modifier = Modifier.height(spacing.lg))
                EditorBodyField(
                    value = editorState.content,
                    placeholder = copy.bodyPlaceholder,
                    textColor = palette.content,
                    onValueChange = onContentChange,
                )
                Spacer(modifier = Modifier.height(spacing.md))
                Text(
                    text = editorState.updatedAt.toEditedLabel(copy),
                    style = typography.label,
                    color = palette.content.copy(alpha = 0.56f),
                )
                if (lastMessage != null) {
                    Spacer(modifier = Modifier.height(spacing.sm))
                    Text(
                        text = lastMessage,
                        style = typography.label,
                        color = palette.content.copy(alpha = 0.72f),
                    )
                }
                Spacer(modifier = Modifier.height(spacing.xl))
                SearchField(
                    value = uiState.searchQuery,
                    copy = copy,
                    palette = palette,
                    onValueChange = onSearchQueryChange,
                )
                Spacer(modifier = Modifier.height(spacing.sm))
                FilterRow(
                    selectedFilter = uiState.filter,
                    copy = copy,
                    onFilterSelected = onFilterSelected,
                )
                Spacer(modifier = Modifier.height(spacing.md + spacing.xxs))
                Text(
                    text = copy.recentNotesTitle,
                    style = typography.sectionTitle,
                    color = palette.content,
                )
                Spacer(modifier = Modifier.height(spacing.sm))
            }
            if (uiState.notes.isEmpty()) {
                item {
                    Text(
                        text = copy.emptyRecentNotes,
                        style = typography.body,
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
                    Spacer(modifier = Modifier.height(spacing.sm))
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
    val spacing = NotesTheme.spacing
    val actionColor = NotesTheme.colors.textPrimary
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
                    .size(spacing.topBarActionSize)
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
                        .size(spacing.topBarActionSize)
                        .semantics { contentDescription = copy.saveAction },
            ) {
                SaveIcon(color = if (canSave) actionColor else disabledActionColor)
            }
            Spacer(modifier = Modifier.width(spacing.xxs))
            Box {
                IconButton(
                    onClick = { isMenuExpanded = true },
                    modifier =
                        Modifier
                            .size(spacing.topBarActionSize)
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
    Canvas(modifier = Modifier.size(NotesTheme.spacing.iconSize)) {
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
    Canvas(modifier = Modifier.size(NotesTheme.spacing.iconSize)) {
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
    Canvas(modifier = Modifier.size(NotesTheme.spacing.iconSize)) {
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
    palette: NotesNotePalette,
    onValueChange: (String) -> Unit,
) {
    val spacing = NotesTheme.spacing
    val typography = NotesTheme.typography
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = palette.card,
        shape = NotesTheme.shapes.small,
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.md, vertical = spacing.sm),
            textStyle =
                typography.body.copy(
                    color = palette.content,
                ),
            cursorBrush = SolidColor(palette.content),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (value.isEmpty()) {
                        Text(
                            text = copy.searchPlaceholder,
                            style = typography.body,
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
        horizontalArrangement = Arrangement.spacedBy(NotesTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NoteFilter.entries.forEach { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = {
                    Text(
                        text = filter.label(copy),
                        style = NotesTheme.typography.caption,
                    )
                },
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
        horizontalArrangement = Arrangement.spacedBy(NotesTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NotesTheme.colors.notePalettes.forEach { palette ->
            val isSelected = palette.key == selectedColorKey
            Box(
                modifier =
                    Modifier
                        .size(NotesTheme.spacing.swatchSize)
                        .clip(CircleShape)
                        .background(palette.background)
                        .border(
                            border =
                                BorderStroke(
                                    width =
                                        if (isSelected) {
                                            NotesTheme.spacing.xxs / 2
                                        } else {
                                            NotesTheme.spacing.xxs / 4
                                        },
                                    color =
                                        if (isSelected) {
                                            palette.content.copy(alpha = 0.42f)
                                        } else {
                                            NotesTheme.colors.border.copy(alpha = 0.72f)
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
            NotesTheme.typography.editorTitle.copy(
                color = textColor,
            ),
        minHeight = NotesTheme.spacing.editorTitleMinHeight,
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
            NotesTheme.typography.editorBody.copy(
                color = textColor,
            ),
        minHeight = NotesTheme.spacing.editorBodyMinHeight,
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
    val colors = NotesTheme.colors
    val spacing = NotesTheme.spacing
    val typography = NotesTheme.typography
    val palette = colors.notePaletteFor(note.colorKey)
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
                .clip(NotesTheme.shapes.card)
                .border(
                    border =
                        BorderStroke(
                            width =
                                if (isSelected) {
                                    spacing.xxs / 2
                                } else {
                                    spacing.xxs / 4
                                },
                            color =
                                if (isSelected) {
                                    palette.content.copy(alpha = 0.3f)
                                } else {
                                    colors.border
                                },
                        ),
                    shape = NotesTheme.shapes.card,
                ).clickable(onClick = onClick),
        color = palette.card,
        shape = NotesTheme.shapes.card,
    ) {
        Column(modifier = Modifier.padding(spacing.md)) {
            Text(
                text = note.title.ifBlank { copy.untitledFallback },
                style = typography.cardTitle,
                color = palette.content,
                textDecoration = titleDecoration,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(spacing.xxs))
            Text(
                text = note.contentPreview.ifBlank { note.updatedAt.toEditedLabel(copy) },
                style = typography.bodySmall,
                color = palette.content.copy(alpha = 0.68f),
                textDecoration = titleDecoration,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(spacing.xs))
            Text(
                text =
                    if (note.isCompleted) {
                        copy.completedStatusLabel
                    } else {
                        copy.activeStatusLabel
                    },
                style = typography.label,
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

private const val ICON_STROKE_WIDTH_RATIO = 0.08f

@Preview
@Composable
private fun NotesEditorScreenPreview() {
    NotesDesignSystem {
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
                        selectedColorKey = NotesColors.LAVENDER,
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
}
