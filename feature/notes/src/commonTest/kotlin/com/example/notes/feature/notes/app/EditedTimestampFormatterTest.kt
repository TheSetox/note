package com.example.notes.feature.notes.app

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EditedTimestampFormatterTest {
    @Test
    fun nullTimestamp_usesUnsavedCopy() {
        assertEquals(NotesUiCopy.English.unsavedTimestamp, null.toEditedLabel(NotesUiCopy.English))
    }

    @Test
    fun counterTimestamp_usesLegacyFallback() {
        assertEquals("Edited #42", 42L.toEditedLabel(NotesUiCopy.English))
    }

    @Test
    fun epochTimestamp_usesFormattedDateText() {
        val label = 1_777_071_491_000L.toEditedLabel(NotesUiCopy.English)

        assertTrue(label.startsWith("Edited "))
        assertFalse(label.contains("#"))
        assertTrue(label.contains("/"))
        assertTrue(label.contains(":"))
    }
}
