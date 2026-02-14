package com.example.notes.core.database.source

import com.example.notes.core.database.entity.NoteEntity

interface NotesLocalDataSource {
    suspend fun readAll(): List<NoteEntity>

    suspend fun writeAll(notes: List<NoteEntity>)
}
