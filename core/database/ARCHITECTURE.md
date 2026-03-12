# core:database Architecture

## Module Dependency Diagram

```mermaid
graph LR
  featureNotes["feature:notes"] --> coreDatabase["core:database"]
  coreDatabase --> coreCommon["core:common"]
```

## Class Diagram

```mermaid
classDiagram
  class NoteEntity {
    +id: String
    +title: String
    +content: String
    +isCompleted: Boolean
    +createdAt: Long
    +updatedAt: Long
  }

  class NotesLocalDataSource {
    <<interface>>
    +readAll() List~NoteEntity~
    +writeAll(notes)
  }

  class JsonFileNotesLocalDataSource {
    -dispatchers: AppDispatchers
    -fileSystem: FileSystem
    -filePath: Path
    +readAll() List~NoteEntity~
    +writeAll(notes)
  }

  class FileSystemProvider {
    <<expect/actual>>
    +platformFileSystem() FileSystem
  }

  NotesLocalDataSource <|.. JsonFileNotesLocalDataSource
  JsonFileNotesLocalDataSource --> NoteEntity
  JsonFileNotesLocalDataSource --> FileSystemProvider
```

## Sequence Diagram

```mermaid
sequenceDiagram
  participant Repo as PersistentNotesRepository
  participant Local as JsonFileNotesLocalDataSource
  participant FS as Okio FileSystem
  participant Json as kotlinx.serialization

  Repo->>Local: writeAll(noteEntities)
  Local->>Json: encodeToString(List<NoteEntity>)
  Json-->>Local: JSON payload
  Local->>FS: createDirectories(parent)
  Local->>FS: sink(file).writeUtf8(payload)
  FS-->>Local: write complete
  Local-->>Repo: success
```

## Quality Tasks
- Run module formatting with `./gradlew :core:database:spotlessCheck`.
- Keep data source contract KDoc synchronized with persistence behavior.
