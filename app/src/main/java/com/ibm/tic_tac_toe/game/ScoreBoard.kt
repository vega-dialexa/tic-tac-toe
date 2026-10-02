package com.ibm.tic_tac_toe.game

/** Running tally of outcomes across games, preserved when a single game resets. */
data class ScoreBoard(
    val xWins: Int = 0,
    val oWins: Int = 0,
    val draws: Int = 0
)
