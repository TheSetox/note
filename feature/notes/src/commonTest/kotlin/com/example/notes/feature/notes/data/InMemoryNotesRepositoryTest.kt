package com.example.notes.feature.notes.data

import com.example.notes.core.common.coroutine.AppDispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class InMemoryNotesRepositoryTest {
    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = TestAppDispatchers(testDispatcher)

    @Test
    fun addUpdateDeleteAndComplete_updatesFlowAndSortOrder() =
        runTest(testDispatcher) {
            val repository = InMemoryNotesRepository(dispatchers = dispatchers)

            val firstAdd = repository.addNote(title = "First", content = "First content")
            val secondAdd = repository.addNote(title = "Second", content = "Second content")
            assertTrue(firstAdd.isSuccess)
            assertTrue(secondAdd.isSuccess)

            var notes = repository.observeNotes().first()
            assertEquals(listOf("Second", "First"), notes.map { it.title })

            val firstNoteId = notes.last().id
            val updateResult = repository.updateNote(id = firstNoteId, title = "First edited", content = "Updated")
            assertTrue(updateResult.isSuccess)

            notes = repository.observeNotes().first()
            assertEquals("First edited", notes.first().title)

            val completeResult = repository.setCompleted(id = firstNoteId, isCompleted = true)
            assertTrue(completeResult.isSuccess)
            assertTrue(
                repository
                    .observeNotes()
                    .first()
                    .first()
                    .isCompleted,
            )

            val deleteResult = repository.deleteNote(firstNoteId)
            assertTrue(deleteResult.isSuccess)
            assertEquals(1, repository.observeNotes().first().size)
        }

    @Test
    fun deleteMissingNote_returnsFailure() =
        runTest(testDispatcher) {
            val repository = InMemoryNotesRepository(dispatchers = dispatchers)
            val result = repository.deleteNote("missing-note")
            assertTrue(result.isFailure)
        }

    private class TestAppDispatchers(
        testDispatcher: TestDispatcher,
    ) : AppDispatchers {
        override val io = testDispatcher
        override val default = testDispatcher
        override val main = testDispatcher
    }
}
