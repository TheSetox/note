package com.example.notes.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Radius tokens for notes surfaces and controls.
 */
@Immutable
data class NotesShapes(
    val smallRadius: Dp,
    val cardRadius: Dp,
) {
    val small: RoundedCornerShape
        get() = RoundedCornerShape(smallRadius)

    val card: RoundedCornerShape
        get() = RoundedCornerShape(cardRadius)

    companion object {
        fun default(): NotesShapes =
            NotesShapes(
                smallRadius = 8.dp,
                cardRadius = 8.dp,
            )
    }
}
