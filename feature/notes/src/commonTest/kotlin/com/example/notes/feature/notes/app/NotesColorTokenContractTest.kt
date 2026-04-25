package com.example.notes.feature.notes.app

import com.example.notes.core.designsystem.NotesColors
import com.example.notes.feature.notes.domain.NoteColorKeys
import kotlin.test.Test
import kotlin.test.assertEquals

class NotesColorTokenContractTest {
    @Test
    fun noteColorKeys_matchDesignSystemPaletteKeys() {
        assertEquals(
            NoteColorKeys.all,
            NotesColors.light().notePalettes.map { palette -> palette.key },
        )
    }
}
