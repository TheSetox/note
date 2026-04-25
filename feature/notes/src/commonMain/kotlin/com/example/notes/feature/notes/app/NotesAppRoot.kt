@file:Suppress("MagicNumber")

package com.example.notes.feature.notes.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.notes.core.designsystem.NotesDesignSystem
import com.example.notes.core.designsystem.NotesTheme
import com.example.notes.feature.notes.di.notesProdModule
import com.example.notes.feature.notes.presentation.NotesListUiEffect
import com.example.notes.feature.notes.presentation.NotesListViewModel
import org.koin.dsl.koinApplication

/**
 * Root Compose screen for the notes feature used by Android, iOS, and Desktop entry points.
 */
@Composable
fun NotesAppRoot(
    modifier: Modifier = Modifier,
    viewModel: NotesListViewModel = NotesListViewModelProvider(),
    copy: NotesUiCopy = NotesUiCopy.English,
) {
    val uiState by viewModel.uiState.collectAsState()
    val editorState by viewModel.editorState.collectAsState()
    var lastMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(viewModel, copy) {
        viewModel.uiEffects.collect { effect ->
            if (effect is NotesListUiEffect.ShowMessage) {
                lastMessage = copy.messageFor(effect.messageKey)
            }
        }
    }

    NotesDesignSystem {
        val colors = NotesTheme.colors
        MaterialTheme(
            colorScheme =
                lightColorScheme(
                    primary = colors.primary,
                    secondary = colors.secondary,
                    background = colors.appBackground,
                    surface = colors.surface,
                ),
        ) {
            NotesEditorScreen(
                uiState = uiState,
                editorState = editorState,
                lastMessage = lastMessage,
                copy = copy,
                onBackClick = viewModel::startNewNote,
                onSaveClick = viewModel::saveEditor,
                onTitleChange = viewModel::onEditorTitleChanged,
                onContentChange = viewModel::onEditorContentChanged,
                onColorSelected = viewModel::onEditorColorSelected,
                onSearchQueryChange = viewModel::onSearchQueryChanged,
                onFilterSelected = viewModel::onFilterChanged,
                onEditorCompletedChange = viewModel::setEditorNoteCompleted,
                onRequestDelete = viewModel::requestDelete,
                onDismissDelete = viewModel::dismissDeleteConfirmation,
                onConfirmDelete = viewModel::confirmDelete,
                onNoteSelected = viewModel::startEditing,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun NotesListViewModelProvider(): NotesListViewModel {
    val koinApplication =
        remember {
            koinApplication {
                modules(notesProdModule)
            }
        }
    val viewModel =
        remember(koinApplication) {
            koinApplication.koin.get<NotesListViewModel>()
        }

    DisposableEffect(koinApplication, viewModel) {
        onDispose {
            viewModel.clear()
            koinApplication.close()
        }
    }

    return viewModel
}

/**
 * IDE preview for [NotesAppRoot].
 */
@Preview
@Composable
private fun NotesAppRootPreview() {
    NotesAppRoot()
}
