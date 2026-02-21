package com.example.notes.feature.notes.data

import com.example.notes.core.common.coroutine.AppDispatchers
import com.example.notes.feature.notes.domain.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.math.max

/**
 * In-memory repository implementation used for tests, previews, and fake wiring.
 */
class InMemoryNotesRepository(
    private val dispatchers: AppDispatchers,
    initialNotes: List<Note> = emptyList(),
) : NotesRepository {
    private val writeLock = Mutex()
    private val notes = MutableStateFlow(initialNotes.sortedByDescending { it.updatedAt })
    private var idSeed: Long = 0L
    private var clock: Long =
        initialNotes.fold(0L) { acc, note ->
            max(acc, max(note.createdAt, note.updatedAt))
        }

    override fun observeNotes(): Flow<List<Note>> = notes.asStateFlow()

    override suspend fun addNote(
        title: String,
        content: String,
    ): Result<Note> =
        withContext(dispatchers.io) {
            runCatching {
                writeLock.withLock {
                    val timestamp = nextTimestamp()
                    val created =
                        Note(
                            id = nextId(timestamp),
                            title = title,
                            content = content,
                            isCompleted = false,
                            createdAt = timestamp,
                            updatedAt = timestamp,
                        )
                    notes.value = (notes.value + created).sortedByDescending { it.updatedAt }
                    created
                }
            }
        }

    override suspend fun updateNote(
        id: String,
        title: String,
        content: String,
    ): Result<Note> =
        withContext(dispatchers.io) {
            runCatching {
                writeLock.withLock {
                    val current = notes.value
                    val existing = current.firstOrNull { it.id == id } ?: throw NoteNotFoundException(id)
                    val updated =
                        existing.copy(
                            title = title,
                            content = content,
                            updatedAt = nextTimestamp(),
                        )

                    notes.value =
                        current
                            .map { note -> if (note.id == id) updated else note }
                            .sortedByDescending { it.updatedAt }
                    updated
                }
            }
        }

    override suspend fun deleteNote(id: String): Result<Unit> =
        withContext(dispatchers.io) {
            runCatching {
                writeLock.withLock {
                    val current = notes.value
                    if (current.none { it.id == id }) {
                        throw NoteNotFoundException(id)
                    }
                    notes.value = current.filterNot { it.id == id }.sortedByDescending { it.updatedAt }
                }
            }
        }

    override suspend fun setCompleted(
        id: String,
        isCompleted: Boolean,
    ): Result<Note> =
        withContext(dispatchers.io) {
            runCatching {
                writeLock.withLock {
                    val current = notes.value
                    val existing = current.firstOrNull { it.id == id } ?: throw NoteNotFoundException(id)
                    val updated =
                        existing.copy(
                            isCompleted = isCompleted,
                            updatedAt = nextTimestamp(),
                        )

                    notes.value =
                        current
                            .map { note -> if (note.id == id) updated else note }
                            .sortedByDescending { it.updatedAt }
                    updated
                }
            }
        }

    private fun nextId(timestamp: Long): String {
        idSeed += 1
        return "note-$timestamp-$idSeed"
    }

    private fun nextTimestamp(): Long {
        clock += 1
        return clock
    }
}
