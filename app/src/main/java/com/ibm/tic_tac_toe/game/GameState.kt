package com.ibm.tic_tac_toe.game

/**
 * Immutable snapshot of a single game: the 3x3 [board] (index = row * 3 + col),
 * whose turn it is, and the outcome once the game has ended.
 */
data class GameState(
    val board: List<Player?> = List(9) { null },
    val currentPlayer: Player = Player.X,
    val winner: Player? = null,
    val winningLine: List<Int>? = null,
    val isDraw: Boolean = false
) {
    val isGameOver: Boolean get() = winner != null || isDraw
}
