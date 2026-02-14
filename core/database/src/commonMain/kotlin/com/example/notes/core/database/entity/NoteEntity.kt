package com.example.notes.core.database.entity

import kotlinx.serialization.Serializable

@Serializable
data class NoteEntity(
    val id: String,
    val title: String,
    val content: String,
    val isCompleted: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
)
