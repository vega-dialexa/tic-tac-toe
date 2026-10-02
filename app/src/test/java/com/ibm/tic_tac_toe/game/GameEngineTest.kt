package com.ibm.tic_tac_toe.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameEngineTest {

    private fun boardOf(vararg marks: Player?): List<Player?> {
        require(marks.size == 9)
        return marks.toList()
    }

    @Test
    fun `empty board has no winner`() {
        val board = List(9) { null }
        assertNull(GameEngine.findWinningLine(board))
    }

    @Test
    fun `partial non-winning board has no winner`() {
        val board = boardOf(
            Player.X, Player.O, null,
            null, Player.X, null,
            Player.O, null, null
        )
        assertNull(GameEngine.findWinningLine(board))
    }

    @Test
    fun `detects each winning row`() {
        for (row in 0 until 3) {
            val board = MutableList<Player?>(9) { null }
            for (col in 0 until 3) board[row * 3 + col] = Player.X
            assertEquals(listOf(row * 3, row * 3 + 1, row * 3 + 2), GameEngine.findWinningLine(board))
        }
    }

    @Test
    fun `detects each winning column`() {
        for (col in 0 until 3) {
            val board = MutableList<Player?>(9) { null }
            for (row in 0 until 3) board[row * 3 + col] = Player.O
            assertEquals(listOf(col, col + 3, col + 6), GameEngine.findWinningLine(board))
        }
    }

    @Test
    fun `detects the top-left to bottom-right diagonal`() {
        val board = boardOf(
            Player.X, null, null,
            null, Player.X, null,
            null, null, Player.X
        )
        assertEquals(listOf(0, 4, 8), GameEngine.findWinningLine(board))
    }

    @Test
    fun `detects the top-right to bottom-left diagonal`() {
        val board = boardOf(
            null, null, Player.O,
            null, Player.O, null,
            Player.O, null, null
        )
        assertEquals(listOf(2, 4, 6), GameEngine.findWinningLine(board))
    }

    @Test
    fun `a line of three different-looking cells that are not all the same mark is not a win`() {
        val board = boardOf(
            Player.X, Player.X, Player.O,
            null, null, null,
            null, null, null
        )
        assertNull(GameEngine.findWinningLine(board))
    }

    @Test
    fun `isBoardFull is false while a cell is empty`() {
        val board = boardOf(
            Player.X, Player.O, Player.X,
            Player.O, Player.X, Player.O,
            Player.O, Player.X, null
        )
        assertFalse(GameEngine.isBoardFull(board))
    }

    @Test
    fun `isBoardFull is true once every cell is played`() {
        val board = boardOf(
            Player.X, Player.O, Player.X,
            Player.O, Player.X, Player.O,
            Player.O, Player.X, Player.O
        )
        assertTrue(GameEngine.isBoardFull(board))
    }
}
