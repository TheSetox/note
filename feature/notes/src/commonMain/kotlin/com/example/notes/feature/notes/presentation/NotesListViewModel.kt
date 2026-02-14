package com.example.notes.feature.notes.presentation

import com.example.notes.core.common.coroutine.AppDispatchers
import com.example.notes.feature.notes.data.NotesRepository
import com.example.notes.feature.notes.domain.Note
import com.example.notes.feature.notes.domain.NoteFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class NoteListItemUiModel(
    val id: String,
    val title: String,
    val contentPreview: String,
    val isCompleted: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
)

data class NotesListUiState(
    val notes: List<NoteListItemUiModel> = emptyList(),
    val searchQuery: String = "",
    val filter: NoteFilter = NoteFilter.ALL,
    val pendingDeleteNoteId: String? = null,
)

sealed interface NotesListUiEffect {
    data class ShowMessage(
        val messageKey: NotesMessageKey,
    ) : NotesListUiEffect
}

enum class NotesMessageKey {
    TITLE_REQUIRED,
    SAVE_SUCCESS,
    SAVE_FAILURE,
    UPDATE_SUCCESS,
    UPDATE_FAILURE,
    DELETE_SUCCESS,
    DELETE_FAILURE,
    STATUS_UPDATE_SUCCESS,
    STATUS_UPDATE_FAILURE,
}

class NotesListViewModel(
    private val repository: NotesRepository,
    private val dispatchers: AppDispatchers,
    externalScope: CoroutineScope? = null,
) {
    private val ownsScope = externalScope == null
    private val scope = externalScope ?: CoroutineScope(SupervisorJob() + dispatchers.main)

    private val searchQuery = MutableStateFlow("")
    private val selectedFilter = MutableStateFlow(NoteFilter.ALL)
    private val pendingDeleteNoteId = MutableStateFlow<String?>(null)

    private val _uiEffects = MutableSharedFlow<NotesListUiEffect>(extraBufferCapacity = 1)
    val uiEffects: SharedFlow<NotesListUiEffect> = _uiEffects.asSharedFlow()

    val uiState: StateFlow<NotesListUiState> =
        combine(
            repository.observeNotes(),
            searchQuery,
            selectedFilter,
            pendingDeleteNoteId,
        ) { notes, query, filter, deleteId ->
            NotesListUiState(
                notes = notes.toVisibleNotes(query = query, filter = filter),
                searchQuery = query,
                filter = filter,
                pendingDeleteNoteId = deleteId,
            )
        }.stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = NotesListUiState(),
        )

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }

    fun onFilterChanged(filter: NoteFilter) {
        selectedFilter.value = filter
    }

    fun addNote(
        title: String,
        content: String,
    ) {
        val sanitizedTitle = title.trim()
        if (sanitizedTitle.isEmpty()) {
            emitMessage(NotesMessageKey.TITLE_REQUIRED)
            return
        }

        scope.launch(dispatchers.io) {
            val result = repository.addNote(title = sanitizedTitle, content = content.trim())
            emitMessage(
                if (result.isSuccess) {
                    NotesMessageKey.SAVE_SUCCESS
                } else {
                    NotesMessageKey.SAVE_FAILURE
                },
            )
        }
    }

    fun updateNote(
        id: String,
        title: String,
        content: String,
    ) {
        val sanitizedTitle = title.trim()
        if (sanitizedTitle.isEmpty()) {
            emitMessage(NotesMessageKey.TITLE_REQUIRED)
            return
        }

        scope.launch(dispatchers.io) {
            val result = repository.updateNote(id = id, title = sanitizedTitle, content = content.trim())
            emitMessage(
                if (result.isSuccess) {
                    NotesMessageKey.UPDATE_SUCCESS
                } else {
                    NotesMessageKey.UPDATE_FAILURE
                },
            )
        }
    }

    fun setNoteCompleted(
        id: String,
        isCompleted: Boolean,
    ) {
        scope.launch(dispatchers.io) {
            val result = repository.setCompleted(id = id, isCompleted = isCompleted)
            emitMessage(
                if (result.isSuccess) {
                    NotesMessageKey.STATUS_UPDATE_SUCCESS
                } else {
                    NotesMessageKey.STATUS_UPDATE_FAILURE
                },
            )
        }
    }

    fun requestDelete(noteId: String) {
        pendingDeleteNoteId.value = noteId
    }

    fun dismissDeleteConfirmation() {
        pendingDeleteNoteId.value = null
    }

    fun confirmDelete() {
        val noteId = pendingDeleteNoteId.value ?: return
        scope.launch(dispatchers.io) {
            val result = repository.deleteNote(noteId)
            if (result.isSuccess) {
                pendingDeleteNoteId.value = null
            }
            emitMessage(
                if (result.isSuccess) {
                    NotesMessageKey.DELETE_SUCCESS
                } else {
                    NotesMessageKey.DELETE_FAILURE
                },
            )
        }
    }

    fun clear() {
        if (ownsScope) {
            scope.cancel()
        }
    }

    private fun emitMessage(messageKey: NotesMessageKey) {
        scope.launch {
            _uiEffects.emit(NotesListUiEffect.ShowMessage(messageKey))
        }
    }
}

private fun List<Note>.toVisibleNotes(
    query: String,
    filter: NoteFilter,
): List<NoteListItemUiModel> {
    val normalizedQuery = query.trim()

    return asSequence()
        .filter { note ->
            when (filter) {
                NoteFilter.ALL -> true
                NoteFilter.ACTIVE -> !note.isCompleted
                NoteFilter.COMPLETED -> note.isCompleted
            }
        }.filter { note ->
            normalizedQuery.isBlank() ||
                note.title.contains(normalizedQuery, ignoreCase = true) ||
                note.content.contains(normalizedQuery, ignoreCase = true)
        }.sortedByDescending { note -> note.updatedAt }
        .map { note ->
            NoteListItemUiModel(
                id = note.id,
                title = note.title,
                contentPreview = note.content.take(CONTENT_PREVIEW_LENGTH),
                isCompleted = note.isCompleted,
                createdAt = note.createdAt,
                updatedAt = note.updatedAt,
            )
        }.toList()
}

private const val CONTENT_PREVIEW_LENGTH = 120
