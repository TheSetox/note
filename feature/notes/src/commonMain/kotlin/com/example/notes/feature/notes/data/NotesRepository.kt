package com.example.notes.feature.notes.data

import com.example.notes.feature.notes.domain.Note
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    fun observeNotes(): Flow<List<Note>>

    suspend fun addNote(
        title: String,
        content: String,
    ): Result<Note>

    suspend fun updateNote(
        id: String,
        title: String,
        content: String,
    ): Result<Note>

    suspend fun deleteNote(id: String): Result<Unit>

    suspend fun setCompleted(
        id: String,
        isCompleted: Boolean,
    ): Result<Note>
}

class NoteNotFoundException(
    noteId: String,
) : IllegalArgumentException("note_not_found:$noteId")
