package com.ibm.tic_tac_toe.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ibm.tic_tac_toe.game.GameState
import com.ibm.tic_tac_toe.game.Player
import com.ibm.tic_tac_toe.game.ScoreBoard
import com.ibm.tic_tac_toe.game.TicTacToeController
import com.ibm.tic_tac_toe.ui.theme.OColor
import com.ibm.tic_tac_toe.ui.theme.TictactoeTheme
import com.ibm.tic_tac_toe.ui.theme.WinningLineHighlight
import com.ibm.tic_tac_toe.ui.theme.XColor

/**
 * The single screen of the app: status, running score, the board, and the two
 * reset actions. Owns the [TicTacToeController] for its lifetime via [remember].
 */
@Composable
fun GameScreen(modifier: Modifier = Modifier) {
    val controller = remember { TicTacToeController() }
    val gameState = controller.gameState
    val scoreBoard = controller.scoreBoard

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        StatusText(gameState)
        ScoreRow(scoreBoard)
        Board(gameState = gameState, onCellClick = controller::play)
        ActionButtons(
            onNewGame = controller::resetGame,
            onResetScores = controller::resetScores
        )
    }
}

@Composable
private fun StatusText(state: GameState) {
    val text = when {
        state.winner != null -> "${state.winner.name} wins!"
        state.isDraw -> "It's a draw!"
        else -> "${state.currentPlayer.name}'s turn"
    }
    val color = when (state.winner) {
        Player.X -> XColor
        Player.O -> OColor
        null -> MaterialTheme.colorScheme.onSurface
    }
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = color
    )
}

@Composable
private fun ScoreRow(score: ScoreBoard) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ScoreItem(label = "X", value = score.xWins, color = XColor)
        ScoreItem(label = "Draws", value = score.draws, color = MaterialTheme.colorScheme.onSurfaceVariant)
        ScoreItem(label = "O", value = score.oWins, color = OColor)
    }
}

@Composable
private fun ScoreItem(label: String, value: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelLarge, color = color)
        Text(text = value.toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    }
}

/**
 * The 3x3 grid. Sized as a percentage of the available width capped at [MAX_BOARD_SIZE]
 * so the same layout reads well on small phones, large phones, and tablets alike.
 */
@Composable
private fun Board(gameState: GameState, onCellClick: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .widthIn(max = MAX_BOARD_SIZE)
            .aspectRatio(1f)
            .border(2.dp, MaterialTheme.colorScheme.outline)
    ) {
        for (row in 0 until 3) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                for (col in 0 until 3) {
                    val index = row * 3 + col
                    Cell(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        mark = gameState.board[index],
                        isHighlighted = gameState.winningLine?.contains(index) == true,
                        enabled = gameState.board[index] == null && !gameState.isGameOver,
                        onClick = { onCellClick(index) }
                    )
                }
            }
        }
    }
}

private val MAX_BOARD_SIZE = 360.dp

@Composable
private fun Cell(
    modifier: Modifier = Modifier,
    mark: Player?,
    isHighlighted: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
            .background(if (isHighlighted) WinningLineHighlight.copy(alpha = 0.45f) else Color.Transparent)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        val markColor = when (mark) {
            Player.X -> XColor
            Player.O -> OColor
            null -> Color.Transparent
        }
        Text(
            text = mark?.name ?: "",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = markColor
        )
    }
}

@Composable
private fun ActionButtons(onNewGame: () -> Unit, onResetScores: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(onClick = onNewGame) { Text("New Game") }
        OutlinedButton(onClick = onResetScores) { Text("Reset Scores") }
    }
}

@Preview(showBackground = true)
@Composable
private fun GameScreenPreview() {
    TictactoeTheme {
        GameScreen()
    }
}
