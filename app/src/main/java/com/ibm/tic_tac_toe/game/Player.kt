package com.ibm.tic_tac_toe.game

/** One of the two marks a player can place on the board. */
enum class Player {
    X,
    O
}

/** The player who takes the next turn after this one. */
fun Player.other(): Player = if (this == Player.X) Player.O else Player.X
