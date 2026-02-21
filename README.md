# notes-kmp

## Project Module Diagram

```mermaid
graph TD
  androidApp["androidApp"] --> coreUi["core:ui"]
  androidApp --> featureNotes["feature:notes"]

  desktopApp["desktopApp"] --> coreUi
  desktopApp --> featureNotes

  iosApp["iosApp"] -. "bridges NotesShared framework" .-> featureNotes

  featureNotes --> coreCommon["core:common"]
  featureNotes --> coreDatabase["core:database"]
  featureNotes --> coreUi

  coreDatabase --> coreCommon
  coreUi --> coreCommon

  androidApp -. "detektPlugins" .-> toolsDetektRules["tools:detekt-rules"]
  desktopApp -. "detektPlugins" .-> toolsDetektRules
  coreCommon -. "detektPlugins" .-> toolsDetektRules
  coreDatabase -. "detektPlugins" .-> toolsDetektRules
  coreUi -. "detektPlugins" .-> toolsDetektRules
  featureNotes -. "detektPlugins" .-> toolsDetektRules
```

## Module Docs

| Module | README | Architecture |
|---|---|---|
| `androidApp` | [androidApp/README.md](androidApp/README.md) | [androidApp/ARCHITECTURE.md](androidApp/ARCHITECTURE.md) |
| `desktopApp` | [desktopApp/README.md](desktopApp/README.md) | [desktopApp/ARCHITECTURE.md](desktopApp/ARCHITECTURE.md) |
| `iosApp` | [iosApp/README.md](iosApp/README.md) | [iosApp/ARCHITECTURE.md](iosApp/ARCHITECTURE.md) |
| `core:common` | [core/common/README.md](core/common/README.md) | [core/common/ARCHITECTURE.md](core/common/ARCHITECTURE.md) |
| `core:database` | [core/database/README.md](core/database/README.md) | [core/database/ARCHITECTURE.md](core/database/ARCHITECTURE.md) |
| `core:ui` | [core/ui/README.md](core/ui/README.md) | [core/ui/ARCHITECTURE.md](core/ui/ARCHITECTURE.md) |
| `feature:notes` | [feature/notes/README.md](feature/notes/README.md) | [feature/notes/ARCHITECTURE.md](feature/notes/ARCHITECTURE.md) |
| `tools:detekt-rules` | [tools/detekt-rules/README.md](tools/detekt-rules/README.md) | [tools/detekt-rules/ARCHITECTURE.md](tools/detekt-rules/ARCHITECTURE.md) |
