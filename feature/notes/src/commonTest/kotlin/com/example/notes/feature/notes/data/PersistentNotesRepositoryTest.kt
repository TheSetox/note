package com.example.notes.feature.notes.data

import com.example.notes.core.common.coroutine.AppDispatchers
import com.example.notes.core.database.entity.NoteEntity
import com.example.notes.core.database.source.NotesLocalDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PersistentNotesRepositoryTest {
    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = TestAppDispatchers(testDispatcher)

    @Test
    fun observeNotes_loadsPersistedNotesAndSortsDescending() =
        runTest(testDispatcher) {
            val localDataSource =
                FakeNotesLocalDataSource(
                    initialNotes =
                        listOf(
                            NoteEntity(
                                id = "note-1",
                                title = "First",
                                content = "A",
                                isCompleted = false,
                                createdAt = 1L,
                                updatedAt = 1L,
                            ),
                            NoteEntity(
                                id = "note-2",
                                title = "Second",
                                content = "B",
                                isCompleted = false,
                                createdAt = 2L,
                                updatedAt = 2L,
                            ),
                        ),
                )
            val repository = PersistentNotesRepository(localDataSource = localDataSource, dispatchers = dispatchers)

            val notes = repository.observeNotes().first()

            assertEquals(listOf("Second", "First"), notes.map { it.title })
        }

    @Test
    fun addUpdateCompleteDelete_persistsEachMutation() =
        runTest(testDispatcher) {
            val localDataSource = FakeNotesLocalDataSource()
            val repository = PersistentNotesRepository(localDataSource = localDataSource, dispatchers = dispatchers)

            val addResult = repository.addNote(title = "Task", content = "Do thing")
            assertTrue(addResult.isSuccess)
            val noteId = addResult.getOrThrow().id

            val updateResult = repository.updateNote(id = noteId, title = "Task updated", content = "Done soon")
            assertTrue(updateResult.isSuccess)

            val completeResult = repository.setCompleted(id = noteId, isCompleted = true)
            assertTrue(completeResult.isSuccess)

            val deleteResult = repository.deleteNote(id = noteId)
            assertTrue(deleteResult.isSuccess)

            assertTrue(localDataSource.writeSnapshots.size >= 4)
            assertEquals(0, localDataSource.persisted.size)
        }

    private class FakeNotesLocalDataSource(
        initialNotes: List<NoteEntity> = emptyList(),
    ) : NotesLocalDataSource {
        var persisted: List<NoteEntity> = initialNotes
        val writeSnapshots = mutableListOf<List<NoteEntity>>()

        override suspend fun readAll(): List<NoteEntity> = persisted

        override suspend fun writeAll(notes: List<NoteEntity>) {
            persisted = notes
            writeSnapshots += notes
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
