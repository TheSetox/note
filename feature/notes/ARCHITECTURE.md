# feature:notes Architecture

## Module Dependency Diagram

```mermaid
graph LR
  androidApp["androidApp"] --> featureNotes["feature:notes"]
  desktopApp["desktopApp"] --> featureNotes
  iosApp["iosApp"] -. "bridges NotesShared framework" .-> featureNotes

  featureNotes --> coreCommon["core:common"]
  featureNotes --> coreDatabase["core:database"]
  featureNotes --> coreUi["core:ui"]
```

## Class Diagram

```mermaid
classDiagram
  class NotesAppEntry {
    +createNotesListViewModel(repository, dispatchers, scope)
  }

  class NotesListViewModel {
    +uiState: StateFlow~NotesListUiState~
    +editorState: StateFlow~NoteEditorUiState~
    +uiEffects: SharedFlow~NotesListUiEffect~
    +addNote(title, content)
    +updateNote(id, title, content)
    +startNewNote()
    +startEditing(note)
    +onEditorTitleChanged(title)
    +onEditorContentChanged(content)
    +onEditorColorSelected(colorKey)
    +saveEditor()
    +setEditorNoteCompleted(isCompleted)
    +setNoteCompleted(id, isCompleted)
    +requestDelete(noteId)
    +confirmDelete()
  }

  class NoteEditorUiState {
    +activeNoteId: String?
    +title: String
    +content: String
    +selectedColorKey: String
    +isCompleted: Boolean
    +updatedAt: Long?
    +hasUnsavedChanges: Boolean
  }

  class NotesRepository {
    <<interface>>
    +observeNotes()
    +addNote(title, content, colorKey)
    +updateNote(id, title, content, colorKey)
    +deleteNote(id)
    +setCompleted(id, isCompleted)
  }

  class NoteTimestampProvider {
    <<interface>>
    +nowMillis() Long
  }

  class NotesEditorScreen {
    <<composable>>
    +NotesEditorScreen(uiState, editorState, copy, actions)
  }

  class NotesUiCopy {
    +messageFor(messageKey) String
  }

  class PersistentNotesRepository
  class InMemoryNotesRepository
  class NotesLocalDataSource {
    <<interface>>
  }

  class NotesAppRoot {
    <<composable>>
    +NotesAppRoot()
    +NotesAppRootPreview()
  }

  class notesProdModule {
    <<Koin module>>
    +NotesListViewModel
    +NotesRepository
    +NotesLocalDataSource
  }

  NotesAppEntry --> NotesListViewModel
  NotesAppRoot --> NotesEditorScreen
  NotesAppRoot --> notesProdModule
  notesProdModule --> NotesListViewModel
  notesProdModule --> NotesRepository
  NotesEditorScreen --> NoteEditorUiState
  NotesEditorScreen --> NotesUiCopy
  NotesListViewModel --> NotesRepository
  NotesRepository <|.. PersistentNotesRepository
  NotesRepository <|.. InMemoryNotesRepository
  PersistentNotesRepository --> NoteTimestampProvider
  InMemoryNotesRepository --> NoteTimestampProvider
  PersistentNotesRepository --> NotesLocalDataSource
```

## Sequence Diagram

```mermaid
sequenceDiagram
  participant UI as Notes UI
  participant VM as NotesListViewModel
  participant Repo as NotesRepository
  participant Local as NotesLocalDataSource
  participant Flow as observeNotes Flow

  UI->>VM: saveEditor()
  VM->>Repo: addNote(title, content, colorKey)
  Repo->>Repo: timestampProvider.nowMillis()
  Repo->>Local: writeAll(updatedNotes)
  Local-->>Repo: success
  Repo-->>VM: Result.Success(Note)
  Repo-->>Flow: emit updated list
  Flow-->>VM: notes list
  VM-->>UI: updated uiState + editorState + SAVE_SUCCESS effect
```

## Preview Flow

```mermaid
sequenceDiagram
  participant IDE as IDE Preview
  participant Preview as NotesAppRootPreview()
  participant Root as NotesAppRoot()
  participant VM as NotesListViewModel

  IDE->>Preview: render
  Preview->>Root: invoke
  Root->>VM: observe uiState + editorState
  VM-->>Root: editor screen state
  Root-->>IDE: rendered composable tree
```

## Quality Tasks
- Run module formatting with `./gradlew :feature:notes:spotlessCheck`.
- Keep KDoc on repository/viewmodel/screen contracts aligned with behavior changes.
