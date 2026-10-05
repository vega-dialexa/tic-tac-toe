# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Native Android Tic-Tac-Toe built with Kotlin + Jetpack Compose (Material 3). Single-activity,
single-screen app (`MainActivity` -> `ui/GameScreen`). Package `com.ibm.tic_tac_toe`, applicationId
`com.ibm.tic_tac_toe`. `minSdk 34`, `targetSdk`/`compileSdk 37`, Kotlin 2.2.10, AGP 9.3.3.

## Commands

Run all commands from the repo root via the Gradle wrapper (`./gradlew`, not a system `gradle`).

- Build debug APK: `./gradlew assembleDebug`
- Run unit tests (JVM, `app/src/test`): `./gradlew test`
- Run a single unit test class: `./gradlew test --tests "com.ibm.tic_tac_toe.game.GameEngineTest"`
- Run a single unit test method: `./gradlew test --tests "com.ibm.tic_tac_toe.game.GameEngineTest.testName"`
- Run instrumented tests (`app/src/androidTest`, needs a device/emulator): `./gradlew connectedAndroidTest`
- Lint: `./gradlew lint`
- Full check (lint + unit tests): `./gradlew check`

Unit test reports land in `app/build/reports/tests/`; HTML lint report in `app/build/reports/lint-results.html`.

## Architecture

The app is organized in layers, each with a one-way dependency on the one below it. The game and
persistence layers are deliberately plain Kotlin (no Android/Compose imports) so they're unit
testable on the JVM; only `settings`/`persistence` wiring and the `ui`/`audio` packages touch
Android APIs.

- **`game/`** — core rules and session state, no Android dependency.
  - `GameEngine` is a stateless `object`: pure functions over a `List<Player?>` board
    (`findWinningLine`, `isBoardFull`). `WINNING_LINES` enumerates the 8 winning triples.
  - `GameState` is an immutable snapshot (board, current player, winner, winning line, draw flag).
  - `ScoreBoard` is an immutable running tally (xWins/oWins/draws) that survives a single-game reset.
  - `TicTacToeController` is the only mutable piece: a plain class (not an androidx `ViewModel`)
    that holds `gameState`/`scoreBoard` as Compose `State` via `mutableStateOf`, so a composable
    can `remember` one instance and recompose on every move, while still being constructible and
    testable from plain JUnit without Android. `play(index)` is the single mutation entry point —
    it applies a move, asks `GameEngine` for the outcome, and updates both `gameState` and
    `scoreBoard` together.
- **`persistence/`** — survives app close / process death.
  - `KeyValueStore` is a narrow interface (string/int/boolean get/put) abstracting over
    `SharedPreferences`, so repository logic can be unit tested against `FakeKeyValueStore`
    (`app/src/test/.../persistence/FakeKeyValueStore.kt`) instead of real Android storage.
  - `SharedPreferencesStore` is the real `KeyValueStore` impl backed by `android.content.SharedPreferences`.
  - `GameStateRepository` encodes/decodes `GameState`, `ScoreBoard`, `PlayerSettings`, and
    `AppSettings` to/from a `KeyValueStore`, falling back to each type's defaults when a key is
    missing (fresh install). The board is encoded as a 9-char string (`X`/`O`/space).
- **`settings/`** — plain data: `PlayerSettings` (display names + `mySide`, i.e. which mark "I" am
  playing) and `AppSettings` (mute flag + `ThemePreference`: `SYSTEM`/`LIGHT`/`DARK`).
- **`ui/`** — Compose screen and widgets.
  - `GameScreen` is the composition root for session state: it builds the `GameStateRepository`
    from `LocalContext`, `remember`s one `TicTacToeController` for the process lifetime, loads
    `PlayerSettings`/`AppSettings` once, and writes each back to the repository via
    `LaunchedEffect` whenever it changes. It also owns the `TictactoeTheme`/`Scaffold` wrapper and
    resolves `ThemePreference` against `isSystemInDarkTheme()`, and owns/releases the
    `GameSoundPlayer` via `DisposableEffect`.
  - `GameBoard` renders the 3x3 grid; its `Cell` composable animates mark placement
    (scale/fade in) and pulses winning-line cells, but skips the placement animation for marks
    restored from a persisted game at launch.
  - `SettingsDialog` edits `PlayerSettings` + `AppSettings` together, confirmed as one pair back to
    `GameScreen`.
- **`audio/`** — `GameSoundPlayer` synthesizes short tones with `android.media.ToneGenerator`
  (place/win/draw) rather than bundling audio files; it holds a native resource and must have
  `release()` called when done (see `GameScreen`'s `DisposableEffect`). Swapping in a
  `SoundPool`-backed implementation later shouldn't require changing any call site.

### Data flow for a move

`GameBoard` cell tap -> `GameScreen`'s `onCellClick` checks legality against the current
`gameState` -> `TicTacToeController.play(index)` mutates `gameState`/`scoreBoard` -> Compose
recomposes `GameScreen` from the new `State` -> `LaunchedEffect`s persist the new `gameState`/
`scoreBoard` via `GameStateRepository` -> `GameScreen` plays the matching sound based on the
post-move `gameState`.

## Roadmap

See `ROADMAP.md`. Phases 1 (state persistence, player names, turn highlight) and 2 (placement/win
animations, sound effects, theme toggle) are done. Phase 3 (an AI opponent with Easy/Medium/Hard
difficulty, Hard via minimax) is the next planned work; `PlayerSettings.mySide` already exists as
the seam that phase will reuse to mean "the side the human plays" vs. the AI's side.
