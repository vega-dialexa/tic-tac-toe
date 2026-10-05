package com.ibm.tic_tac_toe.settings

/** Which theme the app should render: follow the system, or force one explicitly. */
enum class ThemePreference {
    SYSTEM,
    LIGHT,
    DARK
}

/** App-wide preferences that aren't tied to a specific player. */
data class AppSettings(
    val isMuted: Boolean = false,
    val themePreference: ThemePreference = ThemePreference.SYSTEM
)
