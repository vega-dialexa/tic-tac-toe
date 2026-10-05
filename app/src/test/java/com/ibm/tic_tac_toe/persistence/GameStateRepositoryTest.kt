package com.ibm.tic_tac_toe.persistence

import com.ibm.tic_tac_toe.game.GameState
import com.ibm.tic_tac_toe.game.Player
import com.ibm.tic_tac_toe.game.ScoreBoard
import com.ibm.tic_tac_toe.settings.AppSettings
import com.ibm.tic_tac_toe.settings.PlayerSettings
import com.ibm.tic_tac_toe.settings.ThemePreference
import org.junit.Assert.assertEquals
import org.junit.Test

class GameStateRepositoryTest {

    @Test
    fun `fresh store returns defaults`() {
        val repository = GameStateRepository(FakeKeyValueStore())

        assertEquals(GameState(), repository.loadGameState())
        assertEquals(ScoreBoard(), repository.loadScoreBoard())
        assertEquals(PlayerSettings(), repository.loadPlayerSettings())
        assertEquals(AppSettings(), repository.loadAppSettings())
    }

    @Test
    fun `round-trips an in-progress game`() {
        val repository = GameStateRepository(FakeKeyValueStore())
        val state = GameState(
            board = listOf(
                Player.X, Player.O, null,
                null, Player.X, null,
                null, null, null
            ),
            currentPlayer = Player.O
        )

        repository.saveGameState(state)

        assertEquals(state, repository.loadGameState())
    }

    @Test
    fun `round-trips a won game including the winning line`() {
        val repository = GameStateRepository(FakeKeyValueStore())
        val state = GameState(
            board = listOf(
                Player.X, Player.X, Player.X,
                Player.O, Player.O, null,
                null, null, null
            ),
            currentPlayer = Player.O,
            winner = Player.X,
            winningLine = listOf(0, 1, 2)
        )

        repository.saveGameState(state)

        assertEquals(state, repository.loadGameState())
    }

    @Test
    fun `round-trips a drawn game`() {
        val repository = GameStateRepository(FakeKeyValueStore())
        val state = GameState(
            board = listOf(
                Player.X, Player.O, Player.X,
                Player.X, Player.O, Player.O,
                Player.O, Player.X, Player.X
            ),
            isDraw = true
        )

        repository.saveGameState(state)

        assertEquals(state, repository.loadGameState())
    }

    @Test
    fun `round-trips a non-default score board`() {
        val repository = GameStateRepository(FakeKeyValueStore())
        val score = ScoreBoard(xWins = 3, oWins = 1, draws = 2)

        repository.saveScoreBoard(score)

        assertEquals(score, repository.loadScoreBoard())
    }

    @Test
    fun `round-trips non-default player settings`() {
        val repository = GameStateRepository(FakeKeyValueStore())
        val settings = PlayerSettings(myName = "Alice", opponentName = "Bob", mySide = Player.O)

        repository.savePlayerSettings(settings)

        assertEquals(settings, repository.loadPlayerSettings())
    }

    @Test
    fun `round-trips a muted, dark-theme app settings`() {
        val repository = GameStateRepository(FakeKeyValueStore())
        val settings = AppSettings(isMuted = true, themePreference = ThemePreference.DARK)

        repository.saveAppSettings(settings)

        assertEquals(settings, repository.loadAppSettings())
    }

    @Test
    fun `round-trips an unmuted, light-theme app settings`() {
        val repository = GameStateRepository(FakeKeyValueStore())
        val settings = AppSettings(isMuted = false, themePreference = ThemePreference.LIGHT)

        repository.saveAppSettings(settings)

        assertEquals(settings, repository.loadAppSettings())
    }
}
