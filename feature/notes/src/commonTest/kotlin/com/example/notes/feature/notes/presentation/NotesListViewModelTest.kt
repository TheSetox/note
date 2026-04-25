package com.example.notes.feature.notes.presentation

import com.example.notes.core.common.coroutine.AppDispatchers
import com.example.notes.feature.notes.data.InMemoryNotesRepository
import com.example.notes.feature.notes.domain.NoteColorKeys
import com.example.notes.feature.notes.domain.NoteFilter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class NotesListViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = TestAppDispatchers(testDispatcher)

    @Test
    fun blankTitle_emitsValidationEffect_andDoesNotCreateNote() =
        runTest(testDispatcher) {
            val repository = InMemoryNotesRepository(dispatchers = dispatchers)
            val viewModel = NotesListViewModel(repository = repository, dispatchers = dispatchers)
            try {
                val effectDeferred = async { viewModel.uiEffects.first() }
                viewModel.addNote(title = "   ", content = "body")
                advanceUntilIdle()

                val effect = effectDeferred.await() as NotesListUiEffect.ShowMessage
                assertEquals(NotesMessageKey.TITLE_REQUIRED, effect.messageKey)
                assertEquals(0, viewModel.uiState.value.notes.size)
            } finally {
                viewModel.clear()
            }
        }

    @Test
    fun filterAndSearch_followUpdates_andPreserveSelection() =
        runTest(testDispatcher) {
            val repository = InMemoryNotesRepository(dispatchers = dispatchers)
            val viewModel = NotesListViewModel(repository = repository, dispatchers = dispatchers)
            try {
                viewModel.addNote(title = "Buy milk", content = "from store")
                viewModel.addNote(title = "Archive logs", content = "nightly batch")
                advanceUntilIdle()

                var state = viewModel.uiState.value
                assertEquals(listOf("Archive logs", "Buy milk"), state.notes.map { it.title })

                val toCompleteId = state.notes.last().id
                viewModel.setNoteCompleted(id = toCompleteId, isCompleted = true)
                advanceUntilIdle()

                viewModel.onFilterChanged(NoteFilter.ACTIVE)
                advanceUntilIdle()
                state = viewModel.uiState.value
                assertEquals(1, state.notes.size)
                assertEquals("Archive logs", state.notes.first().title)

                viewModel.onSearchQueryChanged("arch")
                advanceUntilIdle()
                state = viewModel.uiState.value
                assertEquals(NoteFilter.ACTIVE, state.filter)
                assertEquals("arch", state.searchQuery)
                assertEquals(1, state.notes.size)

                viewModel.onSearchQueryChanged("milk")
                advanceUntilIdle()
                assertEquals(0, viewModel.uiState.value.notes.size)
            } finally {
                viewModel.clear()
            }
        }

    @Test
    fun deleteConfirmation_requiresConfirm_andClearsPendingId() =
        runTest(testDispatcher) {
            val repository = InMemoryNotesRepository(dispatchers = dispatchers)
            val viewModel = NotesListViewModel(repository = repository, dispatchers = dispatchers)
            try {
                viewModel.addNote(title = "Read book", content = "chapter 1")
                advanceUntilIdle()

                val noteId =
                    viewModel.uiState.value.notes
                        .first()
                        .id
                viewModel.requestDelete(noteId)
                advanceUntilIdle()
                assertEquals(noteId, viewModel.uiState.value.pendingDeleteNoteId)

                viewModel.confirmDelete()
                advanceUntilIdle()

                assertEquals(null, viewModel.uiState.value.pendingDeleteNoteId)
                assertEquals(0, viewModel.uiState.value.notes.size)
            } finally {
                viewModel.clear()
            }
        }

    @Test
    fun saveEditor_createsNoteAndClearsDirtyState() =
        runTest(testDispatcher) {
            val repository = InMemoryNotesRepository(dispatchers = dispatchers)
            val viewModel = NotesListViewModel(repository = repository, dispatchers = dispatchers)
            try {
                viewModel.onEditorTitleChanged("Project Ideas")
                viewModel.onEditorContentChanged("Mobile note app with soft pop design")
                viewModel.onEditorColorSelected(NoteColorKeys.MINT)
                viewModel.saveEditor()
                advanceUntilIdle()

                val editor = viewModel.editorState.value
                assertEquals("Project Ideas", editor.title)
                assertEquals("Mobile note app with soft pop design", editor.content)
                assertEquals(NoteColorKeys.MINT, editor.selectedColorKey)
                assertEquals(false, editor.hasUnsavedChanges)
                assertEquals(1, viewModel.uiState.value.notes.size)
                assertEquals(
                    NoteColorKeys.MINT,
                    viewModel.uiState.value.notes
                        .first()
                        .colorKey,
                )
            } finally {
                viewModel.clear()
            }
        }

    @Test
    fun startEditingAndSaveEditor_updatesExistingNote() =
        runTest(testDispatcher) {
            val repository = InMemoryNotesRepository(dispatchers = dispatchers)
            val viewModel = NotesListViewModel(repository = repository, dispatchers = dispatchers)
            try {
                viewModel.addNote(title = "Draft", content = "Before")
                advanceUntilIdle()

                viewModel.startEditing(
                    viewModel.uiState.value.notes
                        .first(),
                )
                viewModel.onEditorTitleChanged("Project Ideas")
                viewModel.onEditorContentChanged("After")
                viewModel.onEditorColorSelected(NoteColorKeys.BLUSH)
                viewModel.saveEditor()
                advanceUntilIdle()

                val notes = viewModel.uiState.value.notes
                assertEquals(1, notes.size)
                assertEquals("Project Ideas", notes.first().title)
                assertEquals("After", notes.first().content)
                assertEquals(NoteColorKeys.BLUSH, notes.first().colorKey)
                assertEquals(false, viewModel.editorState.value.hasUnsavedChanges)
            } finally {
                viewModel.clear()
            }
        }

    private class TestAppDispatchers(
        testDispatcher: TestDispatcher,
    ) : AppDispatchers {
        override val io = testDispatcher
        override val default = testDispatcher
        override val main = testDispatcher
    }
}
