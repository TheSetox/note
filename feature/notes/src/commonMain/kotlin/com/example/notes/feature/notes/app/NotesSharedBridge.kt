package com.example.notes.feature.notes.app

import com.example.notes.core.ui.AppBootstrapInfo

class NotesSharedBridge {
    fun bootstrapMessage(): String = "${AppBootstrapInfo.MODULE_ID}:${NotesAppEntry.ENTRY_POINT_ID}"
}
