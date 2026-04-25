package com.example.notes.core.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Color tokens for notes surfaces and editor color palettes.
 */
@Immutable
data class NotesColors(
    val appBackground: Color,
    val surface: Color,
    val surfaceRaised: Color,
    val border: Color,
    val borderStrong: Color,
    val primary: Color,
    val secondary: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val notePalettes: List<NotesNotePalette>,
) {
    fun notePaletteFor(colorKey: String): NotesNotePalette =
        notePalettes.firstOrNull { palette -> palette.key == colorKey }
            ?: notePalettes.first { palette -> palette.key == DEFAULT_NOTE_COLOR_KEY }

    companion object {
        const val WHITE = "white"
        const val CREAM = "cream"
        const val SKY = "sky"
        const val MINT = "mint"
        const val LAVENDER = "lavender"
        const val BLUSH = "blush"
        const val DEFAULT_NOTE_COLOR_KEY = LAVENDER

        fun light(): NotesColors =
            NotesColors(
                appBackground = Color(LAVENDER_BACKGROUND),
                surface = Color(LAVENDER_CARD),
                surfaceRaised = Color(WHITE_CARD),
                border = Color.White.copy(alpha = BORDER_ALPHA),
                borderStrong = Color(PRIMARY).copy(alpha = STRONG_BORDER_ALPHA),
                primary = Color(PRIMARY),
                secondary = Color(SECONDARY),
                textPrimary = Color(LAVENDER_CONTENT),
                textSecondary = Color(LAVENDER_CONTENT).copy(alpha = SECONDARY_TEXT_ALPHA),
                textMuted = Color(LAVENDER_CONTENT).copy(alpha = MUTED_TEXT_ALPHA),
                notePalettes =
                    listOf(
                        NotesNotePalette(
                            key = WHITE,
                            background = Color(WHITE_BACKGROUND),
                            card = Color(WHITE_CARD),
                            content = Color(WHITE_CONTENT),
                        ),
                        NotesNotePalette(
                            key = CREAM,
                            background = Color(CREAM_BACKGROUND),
                            card = Color(CREAM_CARD),
                            content = Color(CREAM_CONTENT),
                        ),
                        NotesNotePalette(
                            key = SKY,
                            background = Color(SKY_BACKGROUND),
                            card = Color(SKY_CARD),
                            content = Color(SKY_CONTENT),
                        ),
                        NotesNotePalette(
                            key = MINT,
                            background = Color(MINT_BACKGROUND),
                            card = Color(MINT_CARD),
                            content = Color(MINT_CONTENT),
                        ),
                        NotesNotePalette(
                            key = LAVENDER,
                            background = Color(LAVENDER_BACKGROUND),
                            card = Color(LAVENDER_CARD),
                            content = Color(LAVENDER_CONTENT),
                        ),
                        NotesNotePalette(
                            key = BLUSH,
                            background = Color(BLUSH_BACKGROUND),
                            card = Color(BLUSH_CARD),
                            content = Color(BLUSH_CONTENT),
                        ),
                    ),
            )
    }
}

@Immutable
data class NotesNotePalette(
    val key: String,
    val background: Color,
    val card: Color,
    val content: Color,
)

private const val BORDER_ALPHA = 0.48f
private const val STRONG_BORDER_ALPHA = 0.3f
private const val SECONDARY_TEXT_ALPHA = 0.72f
private const val MUTED_TEXT_ALPHA = 0.56f

private const val PRIMARY = 0xFF705C7C
private const val SECONDARY = 0xFF5B6F65

private const val WHITE_BACKGROUND = 0xFFFFFBFF
private const val WHITE_CARD = 0xFFFFFFFF
private const val WHITE_CONTENT = 0xFF2C2830

private const val CREAM_BACKGROUND = 0xFFFFF2D7
private const val CREAM_CARD = 0xFFFFF8E8
private const val CREAM_CONTENT = 0xFF3B3021

private const val SKY_BACKGROUND = 0xFFE6F3FF
private const val SKY_CARD = 0xFFF3FAFF
private const val SKY_CONTENT = 0xFF243241

private const val MINT_BACKGROUND = 0xFFE3F7EA
private const val MINT_CARD = 0xFFF1FBF4
private const val MINT_CONTENT = 0xFF26382C

private const val LAVENDER_BACKGROUND = 0xFFF1E5F7
private const val LAVENDER_CARD = 0xFFF9F2FC
private const val LAVENDER_CONTENT = 0xFF30263B

private const val BLUSH_BACKGROUND = 0xFFFFE8EB
private const val BLUSH_CARD = 0xFFFFF4F5
private const val BLUSH_CONTENT = 0xFF3D2830
