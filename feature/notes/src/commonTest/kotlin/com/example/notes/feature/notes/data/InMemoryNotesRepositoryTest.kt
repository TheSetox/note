package com.example.notes.feature.notes.data

import com.example.notes.core.common.coroutine.AppDispatchers
import com.example.notes.feature.notes.domain.NoteColorKeys
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

    @Test
    fun addAndUpdate_preserveSelectedColorKey() =
        runTest(testDispatcher) {
            val repository = InMemoryNotesRepository(dispatchers = dispatchers)

            val added =
                repository
                    .addNote(title = "Color", content = "Mint", colorKey = NoteColorKeys.MINT)
                    .getOrThrow()
            assertEquals(NoteColorKeys.MINT, added.colorKey)

            val updated =
                repository
                    .updateNote(
                        id = added.id,
                        title = "Color",
                        content = "Blush",
                        colorKey = NoteColorKeys.BLUSH,
                    ).getOrThrow()

            assertEquals(NoteColorKeys.BLUSH, updated.colorKey)
            assertEquals(
                NoteColorKeys.BLUSH,
                repository
                    .observeNotes()
                    .first()
                    .first()
                    .colorKey,
            )
        }

    @Test
    fun mutations_useInjectedTimestampProvider() =
        runTest(testDispatcher) {
            val timestampProvider =
                SequenceTimestampProvider(
                    1_777_071_491_000L,
                    1_777_071_492_000L,
                    1_777_071_493_000L,
                )
            val repository =
                InMemoryNotesRepository(
                    dispatchers = dispatchers,
                    timestampProvider = timestampProvider,
                )

            val added = repository.addNote(title = "Timed", content = "Draft").getOrThrow()
            assertEquals(1_777_071_491_000L, added.createdAt)
            assertEquals(1_777_071_491_000L, added.updatedAt)

            val updated =
                repository
                    .updateNote(id = added.id, title = "Timed updated", content = "Saved")
                    .getOrThrow()
            assertEquals(1_777_071_491_000L, updated.createdAt)
            assertEquals(1_777_071_492_000L, updated.updatedAt)

            val completed = repository.setCompleted(id = added.id, isCompleted = true).getOrThrow()
            assertEquals(1_777_071_493_000L, completed.updatedAt)
        }

    private class TestAppDispatchers(
        testDispatcher: TestDispatcher,
    ) : AppDispatchers {
        override val io = testDispatcher
        override val default = testDispatcher
        override val main = testDispatcher
    }

    private class SequenceTimestampProvider(
        private vararg val timestamps: Long,
    ) : NoteTimestampProvider {
        private var index = 0

        override fun nowMillis(): Long {
            val timestamp = timestamps.getOrElse(index) { timestamps.last() }
            index += 1
            return timestamp
        }
    }
}
