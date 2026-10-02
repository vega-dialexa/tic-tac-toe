package com.ibm.tic_tac_toe.game

/**
 * Pure, stateless rules of Tic-Tac-Toe: deciding whether a board has a winning line
 * or is full. Takes no dependency on Android/Compose so it can be unit tested as
 * plain Kotlin.
 */
object GameEngine {

    /** Every triple of board indices that counts as a win: 3 rows, 3 columns, 2 diagonals. */
    val WINNING_LINES: List<List<Int>> = listOf(
        listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // rows
        listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // columns
        listOf(0, 4, 8), listOf(2, 4, 6) // diagonals
    )

    /** Returns the first winning line whose three cells share the same non-null mark, or null. */
    fun findWinningLine(board: List<Player?>): List<Int>? =
        WINNING_LINES.firstOrNull { line ->
            val mark = board[line[0]]
            mark != null && line.all { board[it] == mark }
        }

    /** True once every cell has been played. */
    fun isBoardFull(board: List<Player?>): Boolean = board.all { it != null }
}
