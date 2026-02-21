package com.example.notes.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.notes.feature.notes.app.NotesAppRoot

/**
 * Android entry activity that hosts the shared Compose notes screen.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotesAppRoot()
        }
    }
}
