package com.example.notes.feature.notes.data

import com.example.notes.core.common.coroutine.AppDispatchers
import com.example.notes.core.database.source.NotesLocalDataSource
import com.example.notes.feature.notes.domain.Note
import com.example.notes.feature.notes.domain.NoteColorKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.math.max

/**
 * File-backed repository implementation that persists notes through [NotesLocalDataSource].
 */
class PersistentNotesRepository(
    private val localDataSource: NotesLocalDataSource,
    private val dispatchers: AppDispatchers,
) : NotesRepository {
    private val writeLock = Mutex()
    private val notes = MutableStateFlow<List<Note>>(emptyList())

    private var isLoaded = false
    private var idSeed: Long = 0L
    private var clock: Long = 0L

    override fun observeNotes(): Flow<List<Note>> =
        notes
            .asStateFlow()
            .onStart { ensureLoaded() }

    override suspend fun addNote(
        title: String,
        content: String,
        colorKey: String,
    ): Result<Note> =
        withContext(dispatchers.io) {
            runCatching {
                writeLock.withLock {
                    ensureLoadedLocked()
                    val timestamp = nextTimestamp()
                    val note =
                        Note(
                            id = nextId(timestamp),
                            title = title,
                            content = content,
                            colorKey = colorKey.normalizedColorKey(),
                            isCompleted = false,
                            createdAt = timestamp,
                            updatedAt = timestamp,
                        )
                    persistLocked(nextNotes = notes.value + note)
                    note
                }
            }
        }

    override suspend fun updateNote(
        id: String,
        title: String,
        content: String,
        colorKey: String,
    ): Result<Note> =
        withContext(dispatchers.io) {
            runCatching {
                writeLock.withLock {
                    ensureLoadedLocked()
                    val existing = notes.value.firstOrNull { it.id == id } ?: throw NoteNotFoundException(id)
                    val updated =
                        existing.copy(
                            title = title,
                            content = content,
                            colorKey = colorKey.normalizedColorKey(),
                            updatedAt = nextTimestamp(),
                        )

                    persistLocked(
                        nextNotes =
                            notes.value.map { note ->
                                if (note.id == id) {
                                    updated
                                } else {
                                    note
                                }
                            },
                    )
                    updated
                }
            }
        }

    override suspend fun deleteNote(id: String): Result<Unit> =
        withContext(dispatchers.io) {
            runCatching {
                writeLock.withLock {
                    ensureLoadedLocked()
                    val current = notes.value
                    if (current.none { it.id == id }) {
                        throw NoteNotFoundException(id)
                    }
                    persistLocked(nextNotes = current.filterNot { it.id == id })
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
                    ensureLoadedLocked()
                    val existing = notes.value.firstOrNull { it.id == id } ?: throw NoteNotFoundException(id)
                    val updated =
                        existing.copy(
                            isCompleted = isCompleted,
                            updatedAt = nextTimestamp(),
                        )

                    persistLocked(
                        nextNotes =
                            notes.value.map { note ->
                                if (note.id == id) {
                                    updated
                                } else {
                                    note
                                }
                            },
                    )
                    updated
                }
            }
        }

    private suspend fun ensureLoaded() {
        withContext(dispatchers.io) {
            writeLock.withLock {
                ensureLoadedLocked()
            }
        }
    }

    private suspend fun ensureLoadedLocked() {
        if (isLoaded) {
            return
        }

        val persistedNotes = localDataSource.readAll().map { entity -> entity.toDomain() }
        val sorted = persistedNotes.sortedByDescending { it.updatedAt }
        notes.value = sorted
        idSeed = persistedNotes.size.toLong()
        clock = persistedNotes.fold(0L) { acc, note -> max(acc, max(note.createdAt, note.updatedAt)) }
        isLoaded = true
    }

    private suspend fun persistLocked(nextNotes: List<Note>) {
        val sorted = nextNotes.sortedByDescending { it.updatedAt }
        localDataSource.writeAll(sorted.map { note -> note.toEntity() })
        notes.value = sorted
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

private fun String.normalizedColorKey(): String {
    val isSupportedColor = this in NoteColorKeys.all
    return takeIf { isSupportedColor } ?: NoteColorKeys.LAVENDER
}
