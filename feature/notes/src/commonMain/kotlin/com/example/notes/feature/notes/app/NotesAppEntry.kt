package com.example.notes.feature.notes.app

import com.example.notes.core.common.coroutine.AppDispatchers
import com.example.notes.feature.notes.data.NotesRepository
import com.example.notes.feature.notes.presentation.NotesListViewModel
import kotlinx.coroutines.CoroutineScope

object NotesAppEntry {
    const val ENTRY_POINT_ID: String = "notes_app_entry"

    fun createNotesListViewModel(
        repository: NotesRepository,
        dispatchers: AppDispatchers,
        scope: CoroutineScope? = null,
    ): NotesListViewModel =
        NotesListViewModel(
            repository = repository,
            dispatchers = dispatchers,
            externalScope = scope,
        )
}
