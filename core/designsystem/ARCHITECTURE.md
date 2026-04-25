# core:designsystem Architecture

## Module Dependency Diagram

```mermaid
graph LR
  featureNotes["feature:notes"] --> coreDesignSystem["core:designsystem"]
  coreDesignSystem --> composeRuntime["compose-runtime"]
  coreDesignSystem --> composeFoundation["compose-foundation"]
  coreDesignSystem --> composeUi["compose-ui"]
```

## Class Diagram

```mermaid
classDiagram
  class NotesDesignSystem {
    <<composable>>
    +NotesDesignSystem(tokens, content)
  }

  class NotesDesignTokens
  class NotesTheme {
    <<object>>
    +colors: NotesColors
    +typography: NotesTypography
    +spacing: NotesSpacing
    +shapes: NotesShapes
    +motion: NotesMotion
  }

  class NotesColors
  class NotesNotePalette
  class NotesTypography
  class NotesSpacing
  class NotesShapes
  class NotesMotion

  NotesDesignSystem --> NotesDesignTokens
  NotesDesignSystem --> NotesTheme
  NotesDesignTokens --> NotesColors
  NotesDesignTokens --> NotesTypography
  NotesDesignTokens --> NotesSpacing
  NotesDesignTokens --> NotesShapes
  NotesDesignTokens --> NotesMotion
  NotesTheme --> NotesColors
  NotesTheme --> NotesTypography
  NotesTheme --> NotesSpacing
  NotesTheme --> NotesShapes
  NotesTheme --> NotesMotion
  NotesColors --> NotesNotePalette
```

## Token Flow

```mermaid
sequenceDiagram
  participant Root as App Root
  participant Provider as NotesDesignSystem
  participant Child as Child Composable

  Root->>Provider: Provide token defaults
  Provider->>Child: CompositionLocal values
  Child->>Provider: NotesTheme token reads
```

## Quality Tasks
- Run module formatting with `./gradlew :core:designsystem:spotlessCheck`.
- Keep token default tests aligned with color, spacing, shape, typography, and motion changes.
