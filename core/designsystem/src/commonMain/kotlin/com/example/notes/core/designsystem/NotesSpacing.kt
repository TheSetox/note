package com.example.notes.core.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Spacing and fixed-size tokens for notes UI.
 */
@Immutable
data class NotesSpacing(
    val xxs: Dp,
    val xs: Dp,
    val sm: Dp,
    val md: Dp,
    val lg: Dp,
    val xl: Dp,
    val screenHorizontal: Dp,
    val screenVertical: Dp,
    val topBarActionSize: Dp,
    val iconSize: Dp,
    val swatchSize: Dp,
    val editorTitleMinHeight: Dp,
    val editorBodyMinHeight: Dp,
) {
    companion object {
        fun default(): NotesSpacing =
            NotesSpacing(
                xxs = 4.dp,
                xs = 8.dp,
                sm = 12.dp,
                md = 16.dp,
                lg = 24.dp,
                xl = 30.dp,
                screenHorizontal = 24.dp,
                screenVertical = 16.dp,
                topBarActionSize = 44.dp,
                iconSize = 22.dp,
                swatchSize = 28.dp,
                editorTitleMinHeight = 64.dp,
                editorBodyMinHeight = 360.dp,
            )
    }
}
