# Roadmap

Core gameplay (board, turns, win/draw detection, score counters, reset/reset-scores,
responsive single-screen UI, unit tests for the game logic) is implemented. Phases 1
and 2 are also done; Phase 3 below is the remaining planned follow-up.

## Phase 1 — Game State and User Experience ✅ done

- Persist game state and scores across process death / app restarts, via
  `SharedPreferences` (`persistence/GameStateRepository.kt`).
- Customizable player names (`settings/PlayerSettings.kt`), shown in the status line
  and score row instead of bare "X"/"O".
- Choosing X or O: `PlayerSettings.mySide` plus a settings dialog
  (`ui/SettingsDialog.kt`) to set both names and which mark is "mine".
- A clearer visual indicator for whose turn it is: the current player's score chip
  gets a tinted highlight, on top of the existing status text.

## Phase 2 — Visual and Audio Enhancements ✅ done

- Placement animation: a mark scales/fades in when placed (`ui/GameBoard.kt`'s
  `Cell`); marks restored from a persisted game don't replay the animation on launch.
- Winning-line emphasis: each winning cell briefly pulses (scales up and back) the
  moment the win is detected, on top of the existing highlight color.
- Sound effects for placing a mark, winning, and a draw
  (`audio/GameSoundPlayer.kt`), with a mute switch in the settings dialog. Uses
  Android's `ToneGenerator` to synthesize short tones rather than bundled audio
  files (none were available to add in this environment) — swap in a
  `SoundPool`-backed implementation later for real sound effects, without touching
  any call site.
- A 3-way theme preference (System / Light / Dark) in the settings dialog
  (`settings/AppSettings.kt`), persisted and applied by `ui/GameScreen.kt`, which now
  owns the `TictactoeTheme`/`Scaffold` wrapper itself instead of `MainActivity`.

## Phase 3 — AI Opponent

- A toggle to play against the device instead of a second human.
- Difficulty levels:
  - **Easy** — random valid move.
  - **Medium** — mostly random, but takes an immediate win or block when one is
    available.
  - **Hard** — optimal play via the minimax algorithm (with alpha-beta pruning
    if needed for responsiveness).
