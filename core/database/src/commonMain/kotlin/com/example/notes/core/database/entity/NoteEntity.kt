package com.example.notes.core.database.entity

import kotlinx.serialization.Serializable

@Serializable
data class NoteEntity(
    val id: String,
    val title: String,
    val content: String,
    val colorKey: String = DEFAULT_NOTE_ENTITY_COLOR_KEY,
    val isCompleted: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
)

const val DEFAULT_NOTE_ENTITY_COLOR_KEY: String = "lavender"
