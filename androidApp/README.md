# androidApp

## Purpose
Android entry module for launching the bootstrap app.

## Public Contracts
- `MainActivity`

## Dependencies
- `core:ui`
- `feature:notes`
- `androidx.activity:activity-compose`
- `androidx.core:core-ktx`
- `androidx.appcompat:appcompat`

## Usage Notes
- Build with `./gradlew :androidApp:assembleDebug`.
- `MainActivity` hosts Compose content and renders shared `feature:notes` `notesAppRoot()` UI.

## Fake/Mock Notes
- Uses shared modules and can be wired with fake DI modules in later PRs.

## ProGuard/R8 Notes
- No custom rules added in foundation phase.
