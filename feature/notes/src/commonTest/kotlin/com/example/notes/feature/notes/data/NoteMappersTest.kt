package com.example.notes.feature.notes.data

import com.example.notes.core.database.entity.NoteEntity
import com.example.notes.feature.notes.domain.Note
import kotlin.test.Test
import kotlin.test.assertEquals

class NoteMappersTest {
    @Test
    fun entityToDomain_mapsAllFields() {
        val entity =
            NoteEntity(
                id = "n-1",
                title = "Title",
                content = "Content",
                isCompleted = true,
                createdAt = 10L,
                updatedAt = 20L,
            )

        val domain = entity.toDomain()

        assertEquals("n-1", domain.id)
        assertEquals("Title", domain.title)
        assertEquals("Content", domain.content)
        assertEquals(true, domain.isCompleted)
        assertEquals(10L, domain.createdAt)
        assertEquals(20L, domain.updatedAt)
    }

    @Test
    fun domainToEntity_mapsAllFields() {
        val note =
            Note(
                id = "n-2",
                title = "Another",
                content = "Body",
                isCompleted = false,
                createdAt = 100L,
                updatedAt = 110L,
            )

        val entity = note.toEntity()

        assertEquals("n-2", entity.id)
        assertEquals("Another", entity.title)
        assertEquals("Body", entity.content)
        assertEquals(false, entity.isCompleted)
        assertEquals(100L, entity.createdAt)
        assertEquals(110L, entity.updatedAt)
    }
}
