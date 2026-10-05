package com.ibm.tic_tac_toe.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ibm.tic_tac_toe.game.GameState
import com.ibm.tic_tac_toe.game.Player
import com.ibm.tic_tac_toe.game.ScoreBoard
import com.ibm.tic_tac_toe.settings.PlayerSettings
import com.ibm.tic_tac_toe.ui.theme.OColor
import com.ibm.tic_tac_toe.ui.theme.WinningLineHighlight
import com.ibm.tic_tac_toe.ui.theme.XColor

@Composable
fun StatusText(state: GameState, settings: PlayerSettings) {
    val text = when {
        state.winner != null -> "${settings.nameFor(state.winner)} wins!"
        state.isDraw -> "It's a draw!"
        else -> "${settings.nameFor(state.currentPlayer)}'s turn"
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
fun ScoreRow(score: ScoreBoard, gameState: GameState, settings: PlayerSettings) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ScoreItem(
            label = "${settings.nameFor(Player.X)} (X)",
            value = score.xWins,
            color = XColor,
            isActive = !gameState.isGameOver && gameState.currentPlayer == Player.X
        )
        ScoreItem(
            label = "Draws",
            value = score.draws,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            isActive = false
        )
        ScoreItem(
            label = "${settings.nameFor(Player.O)} (O)",
            value = score.oWins,
            color = OColor,
            isActive = !gameState.isGameOver && gameState.currentPlayer == Player.O
        )
    }
}

/** [isActive] drives the current-turn highlight: a tinted background behind that player's chip. */
@Composable
private fun ScoreItem(label: String, value: Int, color: Color, isActive: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(
                color = if (isActive) color.copy(alpha = 0.15f) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelLarge, color = color, maxLines = 1)
        Text(text = value.toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    }
}

/**
 * The 3x3 grid. Sized as a percentage of the available width capped at [MAX_BOARD_SIZE]
 * so the same layout reads well on small phones, large phones, and tablets alike.
 */
@Composable
fun Board(gameState: GameState, onCellClick: (Int) -> Unit) {
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
private val pulseSpec = tween<Float>(150)

@Composable
private fun Cell(
    modifier: Modifier = Modifier,
    mark: Player?,
    isHighlighted: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val pulse = remember { Animatable(1f) }
    LaunchedEffect(isHighlighted) {
        if (isHighlighted) {
            pulse.animateTo(1.15f, pulseSpec)
            pulse.animateTo(1f, pulseSpec)
        }
    }

    Box(
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
            .background(if (isHighlighted) WinningLineHighlight.copy(alpha = 0.45f) else Color.Transparent)
            .scale(pulse.value)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // AnimatedVisibility seeds its transition with the starting value of `visible`,
        // so a cell restored already-filled from persisted state does not replay this
        // entrance animation on launch — only a newly-placed mark animates in.
        AnimatedVisibility(
            visible = mark != null,
            enter = scaleIn(initialScale = 0.3f) + fadeIn()
        ) {
            val markColor = if (mark == Player.X) XColor else OColor
            Text(
                text = mark?.name ?: "",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = markColor
            )
        }
    }
}

@Composable
fun ActionButtons(onNewGame: () -> Unit, onResetScores: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(onClick = onNewGame) { Text("New Game") }
        OutlinedButton(onClick = onResetScores) { Text("Reset Scores") }
    }
}
