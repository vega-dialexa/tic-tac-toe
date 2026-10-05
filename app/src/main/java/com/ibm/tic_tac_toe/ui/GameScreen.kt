package com.ibm.tic_tac_toe.ui

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ibm.tic_tac_toe.audio.GameSoundPlayer
import com.ibm.tic_tac_toe.game.TicTacToeController
import com.ibm.tic_tac_toe.persistence.GameStateRepository
import com.ibm.tic_tac_toe.persistence.SharedPreferencesStore
import com.ibm.tic_tac_toe.settings.ThemePreference
import com.ibm.tic_tac_toe.ui.theme.TictactoeTheme

private const val PREFS_NAME = "tic_tac_toe_prefs"

/**
 * The single screen of the app. Owns all session state: game progress, score,
 * player settings, and app settings are loaded from on-device storage once via
 * [remember] and written back on every change, so a session survives the app being
 * closed or the process being killed. Also owns the theme (so the user's theme
 * preference can override the system default) and the sound player used for move
 * feedback.
 */
@Composable
fun GameScreen() {
    val context = LocalContext.current
    val repository = remember {
        GameStateRepository(SharedPreferencesStore(context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)))
    }
    val controller = remember { TicTacToeController(repository.loadGameState(), repository.loadScoreBoard()) }
    var playerSettings by remember { mutableStateOf(repository.loadPlayerSettings()) }
    var appSettings by remember { mutableStateOf(repository.loadAppSettings()) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    val soundPlayer = remember { GameSoundPlayer() }
    DisposableEffect(Unit) { onDispose { soundPlayer.release() } }

    LaunchedEffect(controller.gameState) { repository.saveGameState(controller.gameState) }
    LaunchedEffect(controller.scoreBoard) { repository.saveScoreBoard(controller.scoreBoard) }
    LaunchedEffect(playerSettings) { repository.savePlayerSettings(playerSettings) }
    LaunchedEffect(appSettings) { repository.saveAppSettings(appSettings) }

    val darkTheme = when (appSettings.themePreference) {
        ThemePreference.SYSTEM -> isSystemInDarkTheme()
        ThemePreference.LIGHT -> false
        ThemePreference.DARK -> true
    }

    TictactoeTheme(darkTheme = darkTheme) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            val gameState = controller.gameState

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Spacer(Modifier.height(8.dp))
                StatusText(gameState, playerSettings)
                ScoreRow(controller.scoreBoard, gameState, playerSettings)
                Board(
                    gameState = gameState,
                    onCellClick = { index ->
                        val isLegalMove = !gameState.isGameOver && gameState.board[index] == null
                        if (isLegalMove) {
                            controller.play(index)
                            if (!appSettings.isMuted) {
                                val result = controller.gameState
                                when {
                                    result.winner != null -> soundPlayer.playWin()
                                    result.isDraw -> soundPlayer.playDraw()
                                    else -> soundPlayer.playPlace()
                                }
                            }
                        }
                    }
                )
                ActionButtons(
                    onNewGame = controller::resetGame,
                    onResetScores = controller::resetScores
                )
                TextButton(onClick = { showSettingsDialog = true }) {
                    Text("Settings")
                }
            }
        }
    }

    if (showSettingsDialog) {
        SettingsDialog(
            currentPlayerSettings = playerSettings,
            currentAppSettings = appSettings,
            onConfirm = { newPlayerSettings, newAppSettings ->
                playerSettings = newPlayerSettings
                appSettings = newAppSettings
                showSettingsDialog = false
            },
            onDismiss = { showSettingsDialog = false }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GameScreenPreview() {
    GameScreen()
}
