# Roadmap

Core gameplay (board, turns, win/draw detection, score counters, reset/reset-scores,
responsive single-screen UI, unit tests for the game logic) is implemented. Phase 1
is also done; Phases 2 and 3 below are the remaining planned follow-ups, in priority
order.

## Phase 1 — Game State and User Experience ✅ done

- Persist game state and scores across process death / app restarts, via
  `SharedPreferences` (`persistence/GameStateRepository.kt`).
- Customizable player names (`settings/PlayerSettings.kt`), shown in the status line
  and score row instead of bare "X"/"O".
- Choosing X or O: `PlayerSettings.mySide` plus an "Edit Players" dialog
  (`ui/GameScreen.kt`) to set both names and which mark is "mine".
- A clearer visual indicator for whose turn it is: the current player's score chip
  gets a tinted highlight, on top of the existing status text.

## Phase 2 — Visual and Audio Enhancements

- Subtle placement animation when a mark is placed (e.g. scale/fade in).
- The winning line is already highlighted; extend this with a short animated
  emphasis (e.g. a pulse) when the win is detected.
- Sound effects for placing a mark, winning, and a draw, with a mute toggle.
- A user-controlled dark/light theme toggle (currently the app only follows the
  system theme/dynamic color).

## Phase 3 — AI Opponent

- A toggle to play against the device instead of a second human.
- Difficulty levels:
  - **Easy** — random valid move.
  - **Medium** — mostly random, but takes an immediate win or block when one is
    available.
  - **Hard** — optimal play via the minimax algorithm (with alpha-beta pruning
    if needed for responsiveness).
