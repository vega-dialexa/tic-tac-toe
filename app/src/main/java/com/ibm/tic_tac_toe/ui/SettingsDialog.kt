package com.ibm.tic_tac_toe.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ibm.tic_tac_toe.game.Player
import com.ibm.tic_tac_toe.settings.AppSettings
import com.ibm.tic_tac_toe.settings.PlayerSettings
import com.ibm.tic_tac_toe.settings.ThemePreference

/**
 * Players, sound, and theme preferences, all in one dialog over the single game
 * screen. Edits are local until "Save"; dismissing without saving discards them.
 */
@Composable
fun SettingsDialog(
    currentPlayerSettings: PlayerSettings,
    currentAppSettings: AppSettings,
    onConfirm: (PlayerSettings, AppSettings) -> Unit,
    onDismiss: () -> Unit
) {
    var myName by remember { mutableStateOf(currentPlayerSettings.myName) }
    var opponentName by remember { mutableStateOf(currentPlayerSettings.opponentName) }
    var mySide by remember { mutableStateOf(currentPlayerSettings.mySide) }
    var isMuted by remember { mutableStateOf(currentAppSettings.isMuted) }
    var themePreference by remember { mutableStateOf(currentAppSettings.themePreference) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Settings") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Players", style = MaterialTheme.typography.labelLarge)
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
                Text("Play as:")
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

                HorizontalDivider()

                Text("Sound", style = MaterialTheme.typography.labelLarge)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Mute sounds")
                    Switch(checked = isMuted, onCheckedChange = { isMuted = it })
                }

                HorizontalDivider()

                Text("Theme", style = MaterialTheme.typography.labelLarge)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ThemePreference.entries.forEach { preference ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            RadioButton(
                                selected = themePreference == preference,
                                onClick = { themePreference = preference }
                            )
                            Text(preference.name)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(
                    PlayerSettings(
                        myName = myName.ifBlank { currentPlayerSettings.myName },
                        opponentName = opponentName.ifBlank { currentPlayerSettings.opponentName },
                        mySide = mySide
                    ),
                    AppSettings(isMuted = isMuted, themePreference = themePreference)
                )
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
