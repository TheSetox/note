package com.example.notes.core.database.source

import com.example.notes.core.common.coroutine.AppDispatchers
import com.example.notes.core.database.entity.NoteEntity
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer

/**
 * JSON file implementation of [NotesLocalDataSource] using Okio file APIs.
 */
class JsonFileNotesLocalDataSource(
    private val dispatchers: AppDispatchers,
    filePathValue: String = DEFAULT_NOTES_FILE_PATH,
) : NotesLocalDataSource {
    private val filePath: Path = filePathValue.toPath()
    private val fileSystem: FileSystem = platformFileSystem()
    private val json: Json = JsonConfig.default

    override suspend fun readAll(): List<NoteEntity> =
        withContext(dispatchers.io) {
            if (!fileSystem.exists(filePath)) {
                return@withContext emptyList()
            }

            val serializedNotes =
                fileSystem.source(filePath).buffer().let { source ->
                    try {
                        source.readUtf8()
                    } finally {
                        source.close()
                    }
                }

            if (serializedNotes.isBlank()) {
                return@withContext emptyList()
            }

            json.decodeFromString(ListSerializer(NoteEntity.serializer()), serializedNotes)
        }

    override suspend fun writeAll(notes: List<NoteEntity>) {
        withContext(dispatchers.io) {
            val parent = filePath.parent
            if (parent != null && !fileSystem.exists(parent)) {
                fileSystem.createDirectories(parent)
            }

            val serializedNotes =
                json.encodeToString(
                    serializer = ListSerializer(NoteEntity.serializer()),
                    value = notes,
                )

            fileSystem.sink(filePath, mustCreate = false).buffer().let { sink ->
                try {
                    sink.writeUtf8(serializedNotes)
                } finally {
                    sink.close()
                }
            }
        }
    }
}

/**
 * Default relative storage path for notes JSON payload.
 */
const val DEFAULT_NOTES_FILE_PATH = "notes-data/notes.json"

private object JsonConfig {
    val default: Json =
        Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            encodeDefaults = true
        }
}
