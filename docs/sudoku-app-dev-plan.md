# Sudoku Android App — Development Plan
**Architecture:** Clean Architecture (domain / data / presentation)
**UI:** Jetpack Compose
**Purpose:** Small, self-contained steps sized for AI-assisted (Claude Code / Copilot-style) development, each producing a compilable, testable increment.

---

## How to use this plan
Each step is scoped to be a single prompt/session for an AI coding assistant: one clear deliverable, explicit files touched, and a "done when" check. Do steps in order within a phase; phases can be reordered slightly but dependencies are noted.

---

## Phase 0 — Project Setup

**0.1 Initialize project skeleton**
- Create Android Studio project, Kotlin, min SDK 24+, Compose enabled.
- Set up Gradle version catalogs (`libs.versions.toml`).
- Start as single-module with clean *package* separation (`domain`, `data`, `presentation`).
- *Done when:* project builds and runs an empty Compose "Hello Sudoku" screen.

**0.2 Add core dependencies**
- Compose BOM, Navigation-Compose, Hilt, Kotlin Coroutines, kotlinx-serialization, Room.
- Configure Hilt application class + basic DI graph stub.
- *Done when:* app builds with DI container injecting a dummy repository into a dummy ViewModel.

**0.3 Set up testing infrastructure**
- JUnit5 or JUnit4 + Turbine (Flow testing) + Truth/AssertJ + MockK.
- Compose UI testing dependencies + a trivial UI test.
- *Done when:* `./gradlew test` and `./gradlew connectedAndroidTest` (or a sample UI test) pass on a dummy test.

**0.4 CI baseline (optional but recommended)**
- GitHub Actions (or similar) workflow: build + unit tests on push.
- *Done when:* CI runs green on the empty project.

---

## Phase 1 — Domain Layer (pure Kotlin, no Android deps)

**1.1 Define core models**
- `SudokuBoard` (9x9 grid representation), `Cell` (value, isFixed, candidates/notes, position), `Difficulty` enum.
- *Done when:* unit tests confirm model equality/copy semantics and grid indexing (row/col/box).

**1.2 Board validation logic**
- Pure functions: `isValidPlacement(board, row, col, value): Boolean`, `isBoardComplete(board): Boolean`, `findConflicts(board): List<Position>`.
- *Done when:* unit tests cover row/column/box conflicts, edge cases (empty cells, full valid board).

**1.3 Sudoku generator**
- Algorithm to generate a fully solved valid board (backtracking).
- *Done when:* unit test generates N boards, asserts each is fully valid via 1.2's validator.

**1.4 Puzzle carver (difficulty generator)**
- Remove cells from a solved board while preserving unique solvability, parameterized by `Difficulty`.
- Use a solver (see 1.5) to verify uniqueness.
- *Done when:* unit test generates puzzles per difficulty and confirms unique solution + cell-count ranges match difficulty spec.

**1.5 Solver (backtracking + optionally constraint propagation)**
- `solve(board): SudokuBoard?` and `countSolutions(board, limit=2): Int` (for uniqueness checks in 1.4).
- *Done when:* unit tests solve known puzzles (easy/hard/"world's hardest sudoku") within a time bound.

**1.6 Hint engine**
- Function returning the "next logical move" with a technique explanation (naked single, hidden single, etc. — start with naked/hidden singles only).
- *Done when:* unit tests assert hints match expected cell/value on fixture boards.

**1.7 Domain use cases**
- `GenerateNewGameUseCase`, `ValidateMoveUseCase`, `GetHintUseCase`, `CheckWinUseCase`, each as small classes with single `invoke()` operator, no Android imports.
- *Done when:* unit tests per use case using fakes for repository interfaces (defined next).

**1.8 Repository interfaces**
- `SudokuRepository` interface (save/load game state, save stats, list saved games) defined in domain, implemented later in data layer.
- *Done when:* interface compiles, referenced by use cases via constructor injection; no implementation yet.

---

## Phase 2 — Data Layer

**2.1 Room entities & DAO**
- `GameStateEntity`, `CellEntity` (or serialized JSON board column), `GameStatsEntity`.
- DAO with suspend functions + Flow queries.
- *Done when:* Room compiles, migration test or schema export set up, DAO unit tests pass on in-memory DB.

**2.2 DataStore for preferences**
- Theme, sound/haptics toggle, default difficulty, "highlight same numbers" setting, etc.
- *Done when:* a `SettingsDataSource` exposes each pref as a `Flow`, unit-tested with a fake DataStore.

**2.3 Repository implementation**
- `SudokuRepositoryImpl` implementing the domain interface, mapping entities ↔ domain models via mappers.
- *Done when:* repository unit tests (using in-memory Room + fake DataStore) cover save/load/delete/list.

**2.4 Mappers**
- Explicit `toDomain()` / `toEntity()` extension functions, isolated in their own files for testability.
- *Done when:* mapper round-trip tests pass (entity → domain → entity equality).

---

## Phase 3 — Presentation Layer (ViewModels first, UI-agnostic)

**3.1 Game screen state model**
- `GameUiState` data class (board snapshot, selected cell, notes mode, timer, mistake count, difficulty, isComplete, isPaused).
- *Done when:* state class compiles with sensible defaults; no ViewModel yet.

**3.2 GameViewModel — core interactions**
- Cell selection, number input, note toggling, undo/redo stack, pause/resume.
- Use Hilt-injected use cases from Phase 1; expose `StateFlow<GameUiState>`.
- *Done when:* ViewModel unit tests (Turbine) cover: selecting a cell, entering a valid/invalid number, undo, win detection.

**3.3 GameViewModel — hints, timer, persistence**
- Hint request flow, timer tick (coroutine ticker), autosave on background/pause.
- *Done when:* unit tests cover hint invocation updates state, timer increments, autosave triggers repository call (mocked).

**3.4 MenuViewModel**
- New game (difficulty select), continue saved game, settings entry, stats display.
- *Done when:* unit tests cover state exposure for each of these flows using fake repository.

**3.5 StatsViewModel**
- Games played/won, best times per difficulty, streaks.
- *Done when:* unit tests over aggregation logic with fixture stats data.

---

## Phase 4 — UI Layer (Jetpack Compose)

**4.1 Design tokens & theme**
- `Color.kt`, `Typography.kt`, `Shape.kt`, `Theme.kt` (light/dark, dynamic color optional).
- *Done when:* a Compose Preview renders theme swatches correctly.

**4.2 Sudoku grid component**
- `SudokuGrid` composable: draws 9x9 grid with correct 3x3 box borders, given a list of cell states + click callback. No ViewModel dependency — pure UI + state hoisting.
- *Done when:* Compose Preview shows a populated grid; UI test taps a cell and asserts callback invoked with correct coordinates.

**4.3 Cell component + selection/highlight states**
- `SudokuCell`: displays value or notes grid (1-9 small numbers), visual states for selected/peer-highlighted/same-number-highlighted/error/fixed-vs-user-entered.
- *Done when:* Preview gallery shows all visual states side by side.

**4.4 Number pad + controls**
- `NumberPad` (1-9 + erase), toggle for notes mode, undo/redo buttons, hint button, with remaining-count badges per number (optional).
- *Done when:* Preview renders correctly; UI test verifies tapping a number invokes callback.

**4.5 Game screen composition**
- `GameScreen` composable wiring `GameViewModel` state to grid + controls + top bar (timer, difficulty, pause, mistakes).
- *Done when:* screen renders end-to-end against a fake/preview ViewModel; manual smoke test playable on emulator.

**4.6 Pause/overlay & win states**
- Pause overlay (blurred board), win celebration screen/dialog with time + stats summary.
- *Done when:* Previews for both states; UI test triggers win condition (via test hook) and asserts dialog appears.

**4.7 Menu / home screen**
- New game (difficulty picker), continue button (if saved game exists), settings, stats entry points.
- *Done when:* Preview + navigation smoke test from menu to game screen.

**4.8 Settings screen**
- Theme choice, sound/haptics, notes auto-clear behavior, etc., bound to `SettingsDataSource`.
- *Done when:* toggling a setting updates DataStore (verified via test) and UI reflects persisted value on relaunch.

**4.9 Stats screen**
- Simple list/cards of stats per difficulty.
- *Done when:* Preview with fixture data renders correctly.

**4.10 Navigation graph**
- Navigation-Compose graph wiring menu → game → settings/stats, with correct back-stack behavior and saved-game deep-continue.
- *Done when:* navigation UI test covers each transition.

---

## Phase 5 — Polish & Cross-Cutting Concerns

**5.1 Error/edge-case handling**
- Corrupted save recovery, empty states, first-launch experience.
- *Done when:* unit + UI tests cover corrupted-save fallback and first-run flow.

**5.2 Accessibility pass**
- Content descriptions for grid cells, number pad; TalkBack-friendly focus order; sufficient contrast/tap targets.
- *Done when:* manual TalkBack pass + automated a11y checks (Accessibility Test Framework) pass.

**5.3 Animations & feedback**
- Cell entry animation, error shake, win celebration animation, haptics on input/error.
- *Done when:* manual review; animations don't block input during play.

**5.4 Performance pass**
- Recomposition audit (stable/immutable board state, `@Stable`/`@Immutable` annotations, keys in `LazyGrid`/loops), generator/solver performance on background dispatcher.
- *Done when:* Compose layout inspector shows no unnecessary recompositions on cell tap; generation doesn't block UI thread (measured, not just assumed).

**5.5 Persistence edge cases**
- App kill mid-game, rotation, process death — verify `SavedStateHandle`/autosave correctly restores exact state.
- *Done when:* instrumented test kills process and restores expected `GameUiState`.

---

## Phase 6 — Release Readiness

**6.1 App icon, splash screen, metadata**
- *Done when:* app builds a release variant with correct branding.

**6.2 ProGuard/R8 rules + release build verification**
- *Done when:* release build runs correctly on a physical/emulated device with minification on.

**6.3 Store listing assets & versioning**
- Versioning scheme, changelog, screenshots.
- *Done when:* checklist complete, ready for internal testing track.

---

## Suggested working rhythm per step
For each numbered step above, a good AI-assisted prompt structure is:
1. State the step number/goal verbatim from this plan.
2. Point to the specific files/classes expected to change.
3. Ask for tests to be written alongside (or before) implementation.
4. Ask the assistant to run/describe how it verified the "done when" criterion before moving on.

This keeps each unit small enough to review fully, and keeps the dependency direction clean: **domain → data → presentation → UI**, with each layer testable in isolation from the ones above it.
