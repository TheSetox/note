package com.example.notes.feature.notes.data

import kotlin.time.Clock

/**
 * Supplies epoch millisecond timestamps for note mutations.
 */
fun interface NoteTimestampProvider {
    fun nowMillis(): Long
}

object SystemNoteTimestampProvider : NoteTimestampProvider {
    override fun nowMillis(): Long = Clock.System.now().toEpochMilliseconds()
}
