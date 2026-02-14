# desktopApp

## Purpose
Desktop JVM entry module for app bootstrap.

## Public Contracts
- `main` entry point in `Main.kt`

## Dependencies
- `core:ui`
- `feature:notes`

## Usage Notes
- Run with `./gradlew :desktopApp:run`.
- Launches a Compose Desktop window and renders shared `feature:notes` `notesAppRoot()` UI.

## Fake/Mock Notes
- No runtime DI wiring in foundation phase.

## ProGuard/R8 Notes
- N/A for desktop module.
