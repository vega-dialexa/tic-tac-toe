# Roadmap

Core gameplay (board, turns, win/draw detection, score counters, reset/reset-scores,
responsive single-screen UI, unit tests for the game logic) is implemented. The
phases below are the planned follow-ups, in priority order. None of them are
implemented yet.

## Phase 1 — Game State and User Experience

- Persist game state (and scores) across process death / app restarts using
  on-device storage (e.g. DataStore or SharedPreferences).
- Customizable player names, shown in the status line and score row instead of
  bare "X"/"O".
- Let the human player choose to play as X or O before a game starts.
- A clearer visual indicator (beyond the status text) for whose turn it is —
  e.g. highlighting that player's score chip.

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
