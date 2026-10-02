package com.ibm.tic_tac_toe.settings

import com.ibm.tic_tac_toe.game.Player
import org.junit.Assert.assertEquals
import org.junit.Test

class PlayerSettingsTest {

    @Test
    fun `nameFor returns myName for mySide and opponentName for the other mark, when mySide is X`() {
        val settings = PlayerSettings(myName = "Alice", opponentName = "Bob", mySide = Player.X)

        assertEquals("Alice", settings.nameFor(Player.X))
        assertEquals("Bob", settings.nameFor(Player.O))
    }

    @Test
    fun `nameFor returns myName for mySide and opponentName for the other mark, when mySide is O`() {
        val settings = PlayerSettings(myName = "Alice", opponentName = "Bob", mySide = Player.O)

        assertEquals("Bob", settings.nameFor(Player.X))
        assertEquals("Alice", settings.nameFor(Player.O))
    }
}
