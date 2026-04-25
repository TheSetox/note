package com.example.notes.feature.notes.app

internal fun Long?.toEditedLabel(copy: NotesUiCopy): String =
    when {
        this == null -> copy.unsavedTimestamp
        this < MIN_EPOCH_TIMESTAMP_MILLIS -> "${copy.editedPrefix} #$this"
        else -> "${copy.editedPrefix} ${formatEditedTimestamp(epochMillis = this)}"
    }

internal expect fun formatEditedTimestamp(epochMillis: Long): String

private const val MIN_EPOCH_TIMESTAMP_MILLIS = 946_684_800_000L
