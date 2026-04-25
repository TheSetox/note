package com.example.notes.core.designsystem

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NotesDesignSystemTokensTest {
    @Test
    fun defaultColors_includeExpectedNotePalettes() {
        val colors = NotesColors.light()

        assertEquals(Color(0xFFF1E5F7), colors.appBackground)
        assertEquals(NotesColors.LAVENDER, colors.notePaletteFor("missing").key)
        assertEquals(
            listOf(
                NotesColors.WHITE,
                NotesColors.CREAM,
                NotesColors.SKY,
                NotesColors.MINT,
                NotesColors.LAVENDER,
                NotesColors.BLUSH,
            ),
            colors.notePalettes.map { palette -> palette.key },
        )
    }

    @Test
    fun defaultSpacingAndShapes_matchCompactEditorContract() {
        val spacing = NotesSpacing.default()
        val shapes = NotesShapes.default()

        assertEquals(24.dp, spacing.screenHorizontal)
        assertEquals(44.dp, spacing.topBarActionSize)
        assertEquals(64.dp, spacing.editorTitleMinHeight)
        assertEquals(360.dp, spacing.editorBodyMinHeight)
        assertEquals(8.dp, shapes.cardRadius)
    }

    @Test
    fun defaultTypographyAndMotion_areNonEmpty() {
        val typography = NotesTypography.default()
        val motion = NotesMotion.default()

        assertTrue(typography.editorTitle.fontSize.value > typography.body.fontSize.value)
        assertTrue(motion.selectionMillis > 0)
        assertTrue(motion.emphasisMillis >= motion.selectionMillis)
    }
}
