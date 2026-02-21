package com.example.notes.feature.notes.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

/**
 * Root Compose screen for the notes feature used by Android, iOS, and Desktop entry points.
 */
@Composable
fun NotesAppRoot() {
    MaterialTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = NotesSharedBridge().bootstrapMessage())
        }
    }
}

/**
 * IDE preview for [NotesAppRoot].
 */
@Preview
@Composable
private fun NotesAppRootPreview() {
    NotesAppRoot()
}
