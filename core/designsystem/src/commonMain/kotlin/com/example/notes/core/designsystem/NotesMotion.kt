package com.example.notes.core.designsystem

import androidx.compose.runtime.Immutable

/**
 * Motion timing tokens for notes interactions.
 */
@Immutable
data class NotesMotion(
    val selectionMillis: Int,
    val emphasisMillis: Int,
) {
    companion object {
        fun default(): NotesMotion =
            NotesMotion(
                selectionMillis = 160,
                emphasisMillis = 260,
            )
    }
}
