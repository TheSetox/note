package com.example.notes.feature.notes.domain

data class Note(
    val id: String,
    val title: String,
    val content: String,
    val colorKey: String = NoteColorKeys.LAVENDER,
    val isCompleted: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
)

enum class NoteFilter {
    ALL,
    ACTIVE,
    COMPLETED,
}

object NoteColorKeys {
    const val WHITE = "white"
    const val CREAM = "cream"
    const val SKY = "sky"
    const val MINT = "mint"
    const val LAVENDER = "lavender"
    const val BLUSH = "blush"

    val all: List<String> = listOf(WHITE, CREAM, SKY, MINT, LAVENDER, BLUSH)
}
