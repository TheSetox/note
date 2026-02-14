package com.example.notes.core.database.source

import okio.FileSystem
import okio.FileSystem.Companion.SYSTEM

internal actual fun platformFileSystem(): FileSystem = SYSTEM
