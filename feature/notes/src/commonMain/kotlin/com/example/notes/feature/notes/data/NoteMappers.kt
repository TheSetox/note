package com.example.notes.feature.notes.data

import com.example.notes.core.database.entity.NoteEntity
import com.example.notes.feature.notes.domain.Note

internal fun NoteEntity.toDomain(): Note =
    Note(
        id = id,
        title = title,
        content = content,
        isCompleted = isCompleted,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

internal fun Note.toEntity(): NoteEntity =
    NoteEntity(
        id = id,
        title = title,
        content = content,
        isCompleted = isCompleted,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
