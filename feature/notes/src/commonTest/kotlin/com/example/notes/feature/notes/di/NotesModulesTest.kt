package com.example.notes.feature.notes.di

import com.example.notes.feature.notes.presentation.NoteEditorUiState
import com.example.notes.feature.notes.presentation.NotesListUiState
import com.example.notes.feature.notes.presentation.NotesListViewModel
import org.koin.dsl.koinApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class NotesModulesTest {
    @Test
    fun notesProdModule_resolvesNotesListViewModel() {
        val application =
            koinApplication {
                modules(notesProdModule)
            }
        val viewModel = application.koin.get<NotesListViewModel>()

        try {
            assertNotNull(viewModel)
            assertEquals(NotesListUiState(), viewModel.uiState.value)
            assertEquals(NoteEditorUiState(), viewModel.editorState.value)
        } finally {
            viewModel.clear()
            application.close()
        }
    }
}
