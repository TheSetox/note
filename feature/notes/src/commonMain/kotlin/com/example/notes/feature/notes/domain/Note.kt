package com.example.notes.feature.notes.domain

data class Note(
    val id: String,
    val title: String,
    val content: String,
    val isCompleted: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
)

enum class NoteFilter {
    ALL,
    ACTIVE,
    COMPLETED,
}
