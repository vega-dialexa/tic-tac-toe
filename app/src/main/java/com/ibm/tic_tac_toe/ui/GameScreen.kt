package com.ibm.tic_tac_toe.ui

import android.content.Context
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ibm.tic_tac_toe.game.GameState
import com.ibm.tic_tac_toe.game.Player
import com.ibm.tic_tac_toe.game.ScoreBoard
import com.ibm.tic_tac_toe.game.TicTacToeController
import com.ibm.tic_tac_toe.persistence.GameStateRepository
import com.ibm.tic_tac_toe.persistence.SharedPreferencesStore
import com.ibm.tic_tac_toe.settings.PlayerSettings
import com.ibm.tic_tac_toe.ui.theme.OColor
import com.ibm.tic_tac_toe.ui.theme.TictactoeTheme
import com.ibm.tic_tac_toe.ui.theme.WinningLineHighlight
import com.ibm.tic_tac_toe.ui.theme.XColor

private const val PREFS_NAME = "tic_tac_toe_prefs"

/**
 * The single screen of the app: status, running score, the board, the reset
 * actions, and player settings. Game state, score, and player settings are loaded
 * from on-device storage once via [remember] and written back on every change so a
 * session survives the app being closed or the process being killed.
 */
@Composable
fun GameScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val repository = remember {
        GameStateRepository(SharedPreferencesStore(context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)))
    }
    val controller = remember { TicTacToeController(repository.loadGameState(), repository.loadScoreBoard()) }
    var settings by remember { mutableStateOf(repository.loadPlayerSettings()) }
    var showPlayerDialog by remember { mutableStateOf(false) }

    LaunchedEffect(controller.gameState) { repository.saveGameState(controller.gameState) }
    LaunchedEffect(controller.scoreBoard) { repository.saveScoreBoard(controller.scoreBoard) }
    LaunchedEffect(settings) { repository.savePlayerSettings(settings) }

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
        StatusText(gameState, settings)
        ScoreRow(scoreBoard, gameState, settings)
        Board(gameState = gameState, onCellClick = controller::play)
        ActionButtons(
            onNewGame = controller::resetGame,
            onResetScores = controller::resetScores
        )
        TextButton(onClick = { showPlayerDialog = true }) {
            Text("Edit Players")
        }
    }

    if (showPlayerDialog) {
        PlayerSettingsDialog(
            current = settings,
            onConfirm = {
                settings = it
                showPlayerDialog = false
            },
            onDismiss = { showPlayerDialog = false }
        )
    }
}

@Composable
private fun StatusText(state: GameState, settings: PlayerSettings) {
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
private fun ScoreRow(score: ScoreBoard, gameState: GameState, settings: PlayerSettings) {
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

/** Lets the player set both display names and which mark ([PlayerSettings.mySide]) is "mine". */
@Composable
private fun PlayerSettingsDialog(
    current: PlayerSettings,
    onConfirm: (PlayerSettings) -> Unit,
    onDismiss: () -> Unit
) {
    var myName by remember { mutableStateOf(current.myName) }
    var opponentName by remember { mutableStateOf(current.opponentName) }
    var mySide by remember { mutableStateOf(current.mySide) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Players") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = myName,
                    onValueChange = { myName = it },
                    label = { Text("Your name") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = opponentName,
                    onValueChange = { opponentName = it },
                    label = { Text("Opponent name") },
                    singleLine = true
                )
                Text("Play as:", style = MaterialTheme.typography.labelLarge)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Player.entries.forEach { side ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            RadioButton(selected = mySide == side, onClick = { mySide = side })
                            Text(side.name)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(
                    PlayerSettings(
                        myName = myName.ifBlank { current.myName },
                        opponentName = opponentName.ifBlank { current.opponentName },
                        mySide = mySide
                    )
                )
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun GameScreenPreview() {
    TictactoeTheme {
        GameScreen()
    }
}
