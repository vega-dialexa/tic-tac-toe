package com.ibm.tic_tac_toe.persistence

import com.ibm.tic_tac_toe.game.GameState
import com.ibm.tic_tac_toe.game.Player
import com.ibm.tic_tac_toe.game.ScoreBoard
import com.ibm.tic_tac_toe.settings.PlayerSettings

/**
 * Reads and writes [GameState], [ScoreBoard], and [PlayerSettings] to a
 * [KeyValueStore] so a game in progress and the running score survive the app being
 * closed or the process being killed. Falls back to each type's defaults when a key
 * is missing (fresh install).
 */
class GameStateRepository(private val store: KeyValueStore) {

    fun saveGameState(state: GameState) {
        store.putString(KEY_BOARD, encodeBoard(state.board))
        store.putString(KEY_CURRENT_PLAYER, state.currentPlayer.toChar().toString())
        store.putString(KEY_WINNER, state.winner?.toChar()?.toString() ?: "")
        store.putString(KEY_WINNING_LINE, state.winningLine?.joinToString(",") ?: "")
        store.putBoolean(KEY_IS_DRAW, state.isDraw)
    }

    fun loadGameState(): GameState {
        val board = decodeBoard(store.getString(KEY_BOARD, encodeBoard(List(9) { null })))
        val currentPlayer = store.getString(KEY_CURRENT_PLAYER, Player.X.toChar().toString())
            .firstOrNull()?.toPlayerOrNull() ?: Player.X
        val winner = store.getString(KEY_WINNER, "").firstOrNull()?.toPlayerOrNull()
        val winningLine = store.getString(KEY_WINNING_LINE, "")
            .takeIf { it.isNotEmpty() }
            ?.split(",")
            ?.map { it.toInt() }
        val isDraw = store.getBoolean(KEY_IS_DRAW, false)
        return GameState(board, currentPlayer, winner, winningLine, isDraw)
    }

    fun saveScoreBoard(score: ScoreBoard) {
        store.putInt(KEY_X_WINS, score.xWins)
        store.putInt(KEY_O_WINS, score.oWins)
        store.putInt(KEY_DRAWS, score.draws)
    }

    fun loadScoreBoard(): ScoreBoard = ScoreBoard(
        xWins = store.getInt(KEY_X_WINS, 0),
        oWins = store.getInt(KEY_O_WINS, 0),
        draws = store.getInt(KEY_DRAWS, 0)
    )

    fun savePlayerSettings(settings: PlayerSettings) {
        store.putString(KEY_MY_NAME, settings.myName)
        store.putString(KEY_OPPONENT_NAME, settings.opponentName)
        store.putString(KEY_MY_SIDE, settings.mySide.toChar().toString())
    }

    fun loadPlayerSettings(): PlayerSettings {
        val defaults = PlayerSettings()
        return PlayerSettings(
            myName = store.getString(KEY_MY_NAME, defaults.myName),
            opponentName = store.getString(KEY_OPPONENT_NAME, defaults.opponentName),
            mySide = store.getString(KEY_MY_SIDE, defaults.mySide.toChar().toString())
                .firstOrNull()?.toPlayerOrNull() ?: defaults.mySide
        )
    }

    private fun encodeBoard(board: List<Player?>): String =
        board.joinToString("") { (it?.toChar() ?: EMPTY_CELL).toString() }

    private fun decodeBoard(encoded: String): List<Player?> =
        encoded.map { it.toPlayerOrNull() }

    private fun Player.toChar(): Char = if (this == Player.X) 'X' else 'O'

    private fun Char.toPlayerOrNull(): Player? = when (this) {
        'X' -> Player.X
        'O' -> Player.O
        else -> null
    }

    companion object {
        private const val EMPTY_CELL = ' '
        private const val KEY_BOARD = "board"
        private const val KEY_CURRENT_PLAYER = "current_player"
        private const val KEY_WINNER = "winner"
        private const val KEY_WINNING_LINE = "winning_line"
        private const val KEY_IS_DRAW = "is_draw"
        private const val KEY_X_WINS = "x_wins"
        private const val KEY_O_WINS = "o_wins"
        private const val KEY_DRAWS = "draws"
        private const val KEY_MY_NAME = "my_name"
        private const val KEY_OPPONENT_NAME = "opponent_name"
        private const val KEY_MY_SIDE = "my_side"
    }
}
