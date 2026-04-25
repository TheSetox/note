# AGENTS.md

## Purpose

This file is the project-local harness map for `notes-kmp`.

Use it to understand:

1. the Note project's local product and module scope
2. which workspace harness policy is inherited
3. the Note-specific rules that override inherited defaults
4. how module-scoped verification should be selected

Keep this file short. Put reusable operational detail in `../.skills/`, shared references, and harness scripts.

Read this file first for work inside `note`.
Then inherit `../AGENTS.md` as the shared workspace harness map.
When local Note policy conflicts with the inherited workspace harness, this file wins for work inside this project.

## Fixed Repo Profile

- Project: `notes-kmp`
- Product scope: Android, iOS, and desktop clients
- Modules: `androidApp`, `iosApp`, `desktopApp`, `core:common`, `core:database`, `core:designsystem`, `core:ui`, `feature:notes`, and `tools:detekt-rules`
- Architecture: MVVM
- UI pattern: MVVM
- Dependency injection: Koin
- Shared-first rule: `commonMain` first unless platform constraints require otherwise

Only use a different architecture profile or product scope when the user explicitly requests it.

## Golden Principles

These are the Note-local rules that should stay stable:

1. Prefer correctness, safety, and passing verification over speed.
2. Do not add web, Wasm, new platform modules, workflows, build targets, assets, or verification gates unless the user explicitly changes product scope.
3. Respect each module's `README.md` and `ARCHITECTURE.md` before changing boundaries, ownership, dependencies, or public contracts.
4. Keep layer direction intact: Compose UI -> ViewModel -> Repository -> DataSource.
5. Keep notes product behavior in `feature:notes` unless it is app-shell wiring or platform-specific integration.
6. Keep visual tokens in `core:designsystem`; keep shared domain and reusable UI behavior in `core:*` modules unless a narrower feature module clearly owns it.
7. Keep custom static analysis rules in `tools:detekt-rules`.
8. Keep Note-specific policy in this file and reusable workspace knowledge under `../.skills/`.

Read the inherited deeper rules in:

- `../.skills/references/golden-principles.md`
- `../.skills/references/quality-gates.md`
- `../.skills/references/build-bootstrap.md`
- `../.skills/references/git-pr-contract.md`
- `../.skills/references/docs-contract.md`

## System Of Record

Use these layers on purpose:

1. `note/AGENTS.md`
   Project-local policy and overrides for this project.
2. `../AGENTS.md`
   Shared workspace control flow, invariants, and harness entrypoints.
3. module `README.md` and `ARCHITECTURE.md`
   Module ownership, architecture, dependencies, and local contracts.
4. `../.skills/references/`
   Durable repo-wide knowledge shared by multiple skills and agents.
5. `../.skills/<category>/<skill>/`
   Capability memory and role wrappers.
6. `../docs/exec-plans/active/`
   Current decision-complete plans for active work.
7. `../docs/exec-plans/completed/`
   Archived completed plans worth keeping.
8. `../docs/tech-debt.md`
   Deferred cleanup, follow-ups, and known harness debt.

Do not turn `AGENTS.md` back into the encyclopedia.

## Harness Map

### Shared references

- `../.skills/references/golden-principles.md`
- `../.skills/references/repo-map.md`
- `../.skills/references/quality-gates.md`
- `../.skills/references/build-bootstrap.md`
- `../.skills/references/git-pr-contract.md`
- `../.skills/references/docs-contract.md`
- `../.skills/references/memory-policy.md`
- `../.skills/references/agent-roles.md`
- `../.skills/references/handoff-contracts.md`

### Capability skills

- `../.skills/orchestration/workflow/SKILL.md`
- `../.skills/orchestration/reflection-memory-update/SKILL.md`
- `../.skills/architecture/architecture/SKILL.md`
- `../.skills/development/development/SKILL.md`
- `../.skills/testing/testing/SKILL.md`
- `../.skills/workflow/git/SKILL.md`
- `../.skills/build/build/SKILL.md`
- `../.skills/docs/docs/SKILL.md`
- `../.skills/android/android/SKILL.md`

### Role skills

- `../.skills/orchestration/planner/SKILL.md`
- `../.skills/orchestration/builder/SKILL.md`
- `../.skills/orchestration/verifier/SKILL.md`
- `../.skills/orchestration/reviewer/SKILL.md`
- `../.skills/orchestration/gardener/SKILL.md`

### Harness scripts

- `../.skills/scripts/collect_repo_snapshot.sh`
- `../.skills/scripts/generate_skills_catalog.py`
- `../.skills/scripts/validate_knowledge.py`
- `../.skills/scripts/scan_stale_knowledge.py`

### Templates

- `../.skills/templates/kmp-template/`

### Project module docs

- `androidApp/README.md` and `androidApp/ARCHITECTURE.md`
- `desktopApp/README.md` and `desktopApp/ARCHITECTURE.md`
- `iosApp/README.md` and `iosApp/ARCHITECTURE.md`
- `core/common/README.md` and `core/common/ARCHITECTURE.md`
- `core/database/README.md` and `core/database/ARCHITECTURE.md`
- `core/designsystem/README.md` and `core/designsystem/ARCHITECTURE.md`
- `core/ui/README.md` and `core/ui/ARCHITECTURE.md`
- `feature/notes/README.md` and `feature/notes/ARCHITECTURE.md`
- `tools/detekt-rules/README.md` and `tools/detekt-rules/ARCHITECTURE.md`

## Named Agent Roles

These roles are thin wrappers over the same shared harness. Do not create separate role-specific rulebooks.

1. `planner`
   Uses `workflow` to turn vague goals or tickets into approved plans.
2. `builder`
   Uses implementation skills to deliver one approved slice at a time.
3. `verifier`
   Uses testing and build skills to produce the evidence packet.
4. `reviewer`
   Reviews for correctness, architecture, tests, and shipping risk.
5. `gardener`
   Converts repeated lessons and drift signals into better references, scripts, or debt items.

The full role contract lives in `../.skills/references/agent-roles.md`.

## Required Working Flow

For non-trivial work:

1. Start with `../.skills/orchestration/planner/SKILL.md`.
2. Let `planner` load `workflow` and produce a reviewable plan packet.
3. Capture long-running or handoff-heavy work in `../docs/exec-plans/active/`.
4. Use `builder` to implement the next approved slice.
5. Use `verifier` to produce the evidence block and exception handling.
6. Use `reviewer` when a review pass, findings pass, or PR safety pass is needed.
7. Use `gardener` after meaningful phases, CI incidents, debugging waves, or architecture pivots.

Skip the role flow only for trivial one-shot tasks that do not need planning, repo context, or architecture decisions.

## Validation Entry Points

Use scoped module checks unless app wiring, shared build config, or cross-platform behavior requires broader checks.

Before claiming harness changes are complete:

1. Run `../.skills/scripts/generate_skills_catalog.py` only when skills changed.
2. Run `../.skills/scripts/validate_knowledge.py` to check skill packages, shared references, and harness structure when shared harness knowledge changed.
3. Run `../.skills/scripts/scan_stale_knowledge.py` to catch stale names, TODOs, and drift when shared harness knowledge changed.

For code changes, use the quality gate contract in `../.skills/references/quality-gates.md`.

For Note module changes, prefer these local verification defaults:

1. Shared KMP modules: `spotlessCheck`, `:<module>:detekt`, `:<module>:allTests`, `:<module>:assemble`, and `:<module>:koverHtmlReport` when available.
2. Android changes: `:androidApp:testDebugUnitTest`, `:androidApp:assembleDebug`, and `:androidApp:koverHtmlReport` when applicable.
3. Desktop changes: `:desktopApp:test`, `:desktopApp:assemble`, and `:desktopApp:koverHtmlReport` when applicable.
4. iOS changes: `:iosApp:buildIosSimulatorApp`.
5. Detekt rule changes: `:tools:detekt-rules:detekt`, `:tools:detekt-rules:test`, and `:tools:detekt-rules:assemble` when available.

## Memory And Cleanup Rule

Stable repeated lessons should usually go here, in order:

1. shared reference update under `../.skills/references/`
2. helper script or validation improvement under `../.skills/scripts/`
3. capability or role skill update under `../.skills/`
4. `../docs/tech-debt.md`
5. `note/AGENTS.md` only if the Note-local control flow or a true project invariant changed

Use `../.skills/orchestration/reflection-memory-update/SKILL.md` and `../.skills/references/memory-policy.md` to make that decision.

## Path Rule

The user-facing workspace path is `/Users/stephensiapno/IdeaProjects/projects`.
The canonical resolved path is `/Volumes/GameSSD/projects`.

Prefer the user-facing path in communication. Use either path in scripts and commands when the symlink does not matter.
