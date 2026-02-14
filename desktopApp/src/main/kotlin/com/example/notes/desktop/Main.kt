package com.example.notes.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.notes.feature.notes.app.NotesSharedBridge
import com.example.notes.feature.notes.app.notesAppRoot

fun main() =
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = NotesSharedBridge().bootstrapMessage(),
        ) {
            notesAppRoot()
        }
    }
