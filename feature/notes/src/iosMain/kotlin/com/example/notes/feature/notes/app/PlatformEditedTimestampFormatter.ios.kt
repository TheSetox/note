package com.example.notes.feature.notes.app

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale

internal actual fun formatEditedTimestamp(epochMillis: Long): String {
    val formatter =
        NSDateFormatter().apply {
            dateFormat = EDITED_TIMESTAMP_PATTERN
            locale = NSLocale(localeIdentifier = "en_US_POSIX")
        }
    val date =
        NSDate(
            timeIntervalSinceReferenceDate =
                (epochMillis.toDouble() / MILLIS_PER_SECOND) - COCOA_REFERENCE_DATE_SECONDS,
        )
    return formatter.stringFromDate(date)
}

private const val EDITED_TIMESTAMP_PATTERN = "M/d/yyyy, h:mm:ss a"
private const val MILLIS_PER_SECOND = 1_000.0
private const val COCOA_REFERENCE_DATE_SECONDS = 978_307_200.0
