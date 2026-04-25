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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.notes.core.common.coroutine.DefaultAppDispatchers
import com.example.notes.core.database.source.JsonFileNotesLocalDataSource
import com.example.notes.feature.notes.data.PersistentNotesRepository
import com.example.notes.feature.notes.presentation.NotesListUiEffect
import com.example.notes.feature.notes.presentation.NotesListViewModel

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

    MaterialTheme(
        colorScheme =
            lightColorScheme(
                primary = Color(0xFF705C7C),
                secondary = Color(0xFF5B6F65),
                background = Color(0xFFF1E5F7),
                surface = Color(0xFFF9F2FC),
            ),
    ) {
        NotesEditorScreen(
            uiState = uiState,
            editorState = editorState,
            lastMessage = lastMessage,
            copy = copy,
            onBackClick = viewModel::startNewNote,
            onMoreClick = {},
            onSaveClick = viewModel::saveEditor,
            onTitleChange = viewModel::onEditorTitleChanged,
            onContentChange = viewModel::onEditorContentChanged,
            onColorSelected = viewModel::onEditorColorSelected,
            onNoteSelected = viewModel::startEditing,
            modifier = modifier,
        )
    }
}

@Composable
private fun NotesListViewModelProvider(): NotesListViewModel {
    val viewModel =
        remember {
            val dispatchers = DefaultAppDispatchers()
            val localDataSource = JsonFileNotesLocalDataSource(dispatchers = dispatchers)
            val repository = PersistentNotesRepository(localDataSource = localDataSource, dispatchers = dispatchers)
            NotesAppEntry.createNotesListViewModel(
                repository = repository,
                dispatchers = dispatchers,
            )
        }

    DisposableEffect(viewModel) {
        onDispose { viewModel.clear() }
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
