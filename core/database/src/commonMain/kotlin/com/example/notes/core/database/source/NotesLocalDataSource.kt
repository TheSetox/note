package com.example.notes.core.database.source

import com.example.notes.core.database.entity.NoteEntity

/**
 * Local persistence abstraction for reading and writing full note snapshots.
 */
interface NotesLocalDataSource {
    /**
     * Reads all persisted notes.
     */
    suspend fun readAll(): List<NoteEntity>

    /**
     * Writes the full set of notes, replacing previously persisted content.
     */
    suspend fun writeAll(notes: List<NoteEntity>)
}
