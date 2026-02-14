package com.example.notes.feature.notes.di

import com.example.notes.core.common.coroutine.AppDispatchers
import com.example.notes.core.common.coroutine.DefaultAppDispatchers
import com.example.notes.core.database.source.JsonFileNotesLocalDataSource
import com.example.notes.core.database.source.NotesLocalDataSource
import com.example.notes.feature.notes.data.InMemoryNotesRepository
import com.example.notes.feature.notes.data.NotesRepository
import com.example.notes.feature.notes.data.PersistentNotesRepository
import com.example.notes.feature.notes.domain.Note
import com.example.notes.feature.notes.presentation.NotesListViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.dsl.module

val notesProdModule =
    module {
        single<AppDispatchers> { DefaultAppDispatchers() }
        single<NotesLocalDataSource> { JsonFileNotesLocalDataSource(dispatchers = get()) }
        single<NotesRepository> {
            PersistentNotesRepository(
                localDataSource = get(),
                dispatchers = get(),
            )
        }
        factory { (scope: CoroutineScope?) ->
            NotesListViewModel(
                repository = get(),
                dispatchers = get(),
                externalScope = scope,
            )
        }
    }

fun notesTestModule(
    dispatchers: AppDispatchers,
    repository: NotesRepository = InMemoryNotesRepository(dispatchers = dispatchers),
) = module {
    single<AppDispatchers> { dispatchers }
    single<NotesRepository> { repository }
}

fun notesFakeModule(
    dispatchers: AppDispatchers,
    seedNotes: List<Note> = emptyList(),
) = module {
    single<AppDispatchers> { dispatchers }
    single<NotesRepository> { InMemoryNotesRepository(dispatchers = dispatchers, initialNotes = seedNotes) }
}
