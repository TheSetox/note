package com.example.notes.core.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable

/**
 * Complete token set used by the Notes design system.
 */
@Immutable
data class NotesDesignTokens(
    val colors: NotesColors,
    val typography: NotesTypography,
    val spacing: NotesSpacing,
    val shapes: NotesShapes,
    val motion: NotesMotion,
) {
    companion object {
        fun default(): NotesDesignTokens =
            NotesDesignTokens(
                colors = NotesColors.light(),
                typography = NotesTypography.default(),
                spacing = NotesSpacing.default(),
                shapes = NotesShapes.default(),
                motion = NotesMotion.default(),
            )
    }
}

/**
 * Provides Notes design tokens to child composables.
 */
@Composable
fun NotesDesignSystem(
    tokens: NotesDesignTokens = NotesDesignTokens.default(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalNotesColors provides tokens.colors,
        LocalNotesTypography provides tokens.typography,
        LocalNotesSpacing provides tokens.spacing,
        LocalNotesShapes provides tokens.shapes,
        LocalNotesMotion provides tokens.motion,
        content = content,
    )
}

/**
 * Access point for Notes design tokens.
 */
object NotesTheme {
    val colors: NotesColors
        @Composable
        @ReadOnlyComposable
        get() = LocalNotesColors.current

    val typography: NotesTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalNotesTypography.current

    val spacing: NotesSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalNotesSpacing.current

    val shapes: NotesShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalNotesShapes.current

    val motion: NotesMotion
        @Composable
        @ReadOnlyComposable
        get() = LocalNotesMotion.current
}
