package com.example.notes.feature.notes.app

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal actual fun formatEditedTimestamp(epochMillis: Long): String =
    SimpleDateFormat(EDITED_TIMESTAMP_PATTERN, Locale.US).format(Date(epochMillis))

private const val EDITED_TIMESTAMP_PATTERN = "M/d/yyyy, h:mm:ss a"
