# androidApp Architecture

## Module Dependency Diagram

```mermaid
graph LR
  androidApp["androidApp"] --> coreUi["core:ui"]
  androidApp --> featureNotes["feature:notes"]
```

## Class Diagram

```mermaid
classDiagram
  class MainActivity {
    +onCreate(savedInstanceState)
  }

  class NotesAppRoot {
    <<Composable>>
    +NotesAppRoot()
  }

  class NotesSharedBridge {
    +bootstrapMessage() String
  }

  MainActivity --> NotesAppRoot : setContent
  NotesAppRoot ..> NotesSharedBridge : resolves bootstrap text
```

## Sequence Diagram

```mermaid
sequenceDiagram
  participant Android as Android Runtime
  participant Activity as MainActivity
  participant Compose as NotesAppRoot()
  participant Bridge as NotesSharedBridge

  Android->>Activity: onCreate()
  Activity->>Compose: setContent { NotesAppRoot() }
  Compose->>Bridge: bootstrapMessage()
  Bridge-->>Compose: module_id:entry_point_id
  Compose-->>Android: Render shared UI
```

## Quality Tasks
- Run module formatting with `./gradlew :androidApp:spotlessCheck`.
- Keep entry-point KDoc updated when activity responsibilities change.
