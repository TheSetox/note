# iosApp Architecture

## Module Dependency Diagram

```mermaid
graph LR
  iosApp["iosApp"] -. "bridges NotesShared framework" .-> featureNotes["feature:notes"]
```

## Class Diagram

```mermaid
classDiagram
  class NotesIOSApp {
    +body: Scene
  }

  class ContentView {
    +body: View
  }

  class NotesComposeContainer {
    +makeUIViewController(context) UIViewController
    +updateUIViewController(controller, context)
  }

  class NotesViewControllerKt {
    +makeNotesViewController() UIViewController
  }

  NotesIOSApp --> ContentView
  ContentView --> NotesComposeContainer
  NotesComposeContainer --> NotesViewControllerKt : bridge call
```

## Sequence Diagram

```mermaid
sequenceDiagram
  participant iOS as iOS Runtime
  participant App as NotesIOSApp
  participant View as ContentView
  participant Bridge as NotesComposeContainer
  participant Kotlin as makeNotesViewController()

  iOS->>App: launch
  App->>View: create ContentView
  View->>Bridge: embed UIViewControllerRepresentable
  Bridge->>Kotlin: makeNotesViewController()
  Kotlin-->>Bridge: Compose UIViewController
  Bridge-->>iOS: render shared Kotlin UI
```

## Quality Tasks
- Run module formatting with `./gradlew :iosApp:spotlessCheck`.
