package com.ibm.tic_tac_toe.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TicTacToeControllerTest {

    @Test
    fun `turns alternate starting with X`() {
        val controller = TicTacToeController()
        assertEquals(Player.X, controller.gameState.currentPlayer)

        controller.play(0)
        assertEquals(Player.O, controller.gameState.currentPlayer)

        controller.play(1)
        assertEquals(Player.X, controller.gameState.currentPlayer)
    }

    @Test
    fun `playing an already filled cell is ignored`() {
        val controller = TicTacToeController()
        controller.play(0) // X
        controller.play(0) // O attempts the same cell

        assertEquals(Player.X, controller.gameState.board[0])
        assertEquals(Player.O, controller.gameState.currentPlayer)
    }

    @Test
    fun `playing after the game has ended is ignored`() {
        val controller = TicTacToeController()
        // X: 0,1,2 top row win. O: 3,4.
        controller.play(0)
        controller.play(3)
        controller.play(1)
        controller.play(4)
        controller.play(2) // X wins

        assertTrue(controller.gameState.isGameOver)
        val boardAfterWin = controller.gameState.board

        controller.play(5)

        assertEquals(boardAfterWin, controller.gameState.board)
    }

    @Test
    fun `a winning move records the winner, winning line, and score`() {
        val controller = TicTacToeController()
        controller.play(0) // X
        controller.play(3) // O
        controller.play(1) // X
        controller.play(4) // O
        controller.play(2) // X completes top row

        val state = controller.gameState
        assertEquals(Player.X, state.winner)
        assertEquals(listOf(0, 1, 2), state.winningLine)
        assertFalse(state.isDraw)
        assertEquals(1, controller.scoreBoard.xWins)
        assertEquals(0, controller.scoreBoard.oWins)
    }

    @Test
    fun `an O win is scored for O`() {
        val controller = TicTacToeController()
        // X: 0,1,8 (no win). O: 3,4,5 (middle row win).
        controller.play(0) // X
        controller.play(3) // O
        controller.play(1) // X
        controller.play(4) // O
        controller.play(8) // X
        controller.play(5) // O completes middle row

        assertEquals(Player.O, controller.gameState.winner)
        assertEquals(1, controller.scoreBoard.oWins)
        assertEquals(0, controller.scoreBoard.xWins)
    }

    @Test
    fun `a full board with no winner is a draw`() {
        val controller = TicTacToeController()
        // X O X
        // X O O
        // O X X
        val moves = listOf(0, 1, 2, 4, 3, 5, 7, 6, 8)
        moves.forEach(controller::play)

        val state = controller.gameState
        assertTrue(state.isDraw)
        assertNull(state.winner)
        assertTrue(state.isGameOver)
        assertEquals(1, controller.scoreBoard.draws)
    }

    @Test
    fun `resetGame clears the board but keeps the score`() {
        val controller = TicTacToeController()
        controller.play(0)
        controller.play(3)
        controller.play(1)
        controller.play(4)
        controller.play(2) // X wins, score becomes xWins = 1

        controller.resetGame()

        assertEquals(GameState(), controller.gameState)
        assertEquals(1, controller.scoreBoard.xWins)
    }

    @Test
    fun `resetScores zeroes the score without touching the game in progress`() {
        val controller = TicTacToeController()
        controller.play(0)
        controller.play(3)

        controller.resetScores()

        assertEquals(ScoreBoard(), controller.scoreBoard)
        assertEquals(Player.X, controller.gameState.board[0])
        assertEquals(Player.O, controller.gameState.board[3])
    }
}
