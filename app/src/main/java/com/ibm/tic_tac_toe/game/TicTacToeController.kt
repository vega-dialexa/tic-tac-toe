package com.ibm.tic_tac_toe.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Orchestrates one game session: applies moves, delegates win/draw detection to
 * [GameEngine], and keeps the cross-game [ScoreBoard]. Deliberately a plain Kotlin
 * class (not an androidx ViewModel) so it has no Android dependency and can be
 * exercised directly from JVM unit tests; it still exposes its state as Compose
 * [androidx.compose.runtime.State] so a composable can `remember` an instance and
 * recompose automatically on every move.
 */
class TicTacToeController {

    var gameState: GameState by mutableStateOf(GameState())
        private set

    var scoreBoard: ScoreBoard by mutableStateOf(ScoreBoard())
        private set

    /** Places the current player's mark at [index], ignoring the click if it's not legal. */
    fun play(index: Int) {
        val current = gameState
        if (current.isGameOver || current.board[index] != null) return

        val newBoard = current.board.toMutableList().also { it[index] = current.currentPlayer }
        val winningLine = GameEngine.findWinningLine(newBoard)

        gameState = when {
            winningLine != null -> {
                scoreBoard = scoreBoard.withWinFor(current.currentPlayer)
                current.copy(board = newBoard, winner = current.currentPlayer, winningLine = winningLine)
            }
            GameEngine.isBoardFull(newBoard) -> {
                scoreBoard = scoreBoard.copy(draws = scoreBoard.draws + 1)
                current.copy(board = newBoard, isDraw = true)
            }
            else -> current.copy(board = newBoard, currentPlayer = current.currentPlayer.other())
        }
    }

    /** Starts a new game on an empty board. Scores carry over. */
    fun resetGame() {
        gameState = GameState()
    }

    /** Zeroes the score counters without touching the game in progress. */
    fun resetScores() {
        scoreBoard = ScoreBoard()
    }

    private fun ScoreBoard.withWinFor(player: Player): ScoreBoard = when (player) {
        Player.X -> copy(xWins = xWins + 1)
        Player.O -> copy(oWins = oWins + 1)
    }
}
