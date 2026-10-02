package com.ibm.tic_tac_toe.settings

import com.ibm.tic_tac_toe.game.Player

/**
 * Display names for the two marks, plus which one is "mine". With no AI yet both
 * names belong to humans sharing the device, but [mySide] is the seam Phase 3 will
 * reuse to mean "the side the human plays" versus the AI's side.
 */
data class PlayerSettings(
    val myName: String = "Player 1",
    val opponentName: String = "Player 2",
    val mySide: Player = Player.X
) {
    fun nameFor(player: Player): String = if (player == mySide) myName else opponentName
}
