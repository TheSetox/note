# desktopApp Architecture

## Module Dependency Diagram

```mermaid
graph LR
  desktopApp["desktopApp"] --> coreUi["core:ui"]
  desktopApp --> featureNotes["feature:notes"]
```

## Class Diagram

```mermaid
classDiagram
  class MainKt {
    +main()
  }

  class NotesSharedBridge {
    +bootstrapMessage() String
  }

  class NotesAppRoot {
    <<Composable>>
    +NotesAppRoot()
  }

  MainKt --> NotesSharedBridge : window title
  MainKt --> NotesAppRoot : window content
```

## Sequence Diagram

```mermaid
sequenceDiagram
  participant User as Desktop User
  participant Entry as main()
  participant Bridge as NotesSharedBridge
  participant Window as Compose Window
  participant Compose as NotesAppRoot()

  User->>Entry: Launch app
  Entry->>Bridge: bootstrapMessage()
  Bridge-->>Entry: title text
  Entry->>Window: create Window(title)
  Window->>Compose: Render NotesAppRoot()
  Compose-->>User: Shared UI visible
```

## Quality Tasks
- Run module formatting with `./gradlew :desktopApp:spotlessCheck`.
