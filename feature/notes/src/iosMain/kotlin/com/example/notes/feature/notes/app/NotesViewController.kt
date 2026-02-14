package com.example.notes.feature.notes.app

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

fun makeNotesViewController(): UIViewController = ComposeUIViewController { notesAppRoot() }
