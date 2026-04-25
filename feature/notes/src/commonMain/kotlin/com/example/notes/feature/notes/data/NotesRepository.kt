package com.example.notes.feature.notes.data

import com.example.notes.feature.notes.domain.Note
import com.example.notes.feature.notes.domain.NoteColorKeys
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for note CRUD and completion updates.
 */
interface NotesRepository {
    /**
     * Observes all notes sorted by most recently updated first.
     */
    fun observeNotes(): Flow<List<Note>>

    /**
     * Creates a new note.
     */
    suspend fun addNote(
        title: String,
        content: String,
        colorKey: String = NoteColorKeys.LAVENDER,
    ): Result<Note>

    /**
     * Updates an existing note by id.
     */
    suspend fun updateNote(
        id: String,
        title: String,
        content: String,
        colorKey: String = NoteColorKeys.LAVENDER,
    ): Result<Note>

    /**
     * Deletes a note by id.
     */
    suspend fun deleteNote(id: String): Result<Unit>

    /**
     * Sets completion state of a note by id.
     */
    suspend fun setCompleted(
        id: String,
        isCompleted: Boolean,
    ): Result<Note>
}

/**
 * Thrown when an operation targets a note id that does not exist.
 */
class NoteNotFoundException(
    noteId: String,
) : IllegalArgumentException("note_not_found:$noteId")
