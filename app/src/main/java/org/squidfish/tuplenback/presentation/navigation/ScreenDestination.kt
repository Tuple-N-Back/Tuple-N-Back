package org.squidfish.tuplenback.presentation.navigation

import kotlinx.serialization.Serializable
import org.squidfish.tuplenback.games.Game

sealed interface ScreenDestination {
    @Serializable
    data object MainScreen: ScreenDestination

    @Serializable
    data object GameSelectionScreen: ScreenDestination

    // FIXME: game is temporarily nullable until passing data between screens is reworked
    @Serializable
    data class GameScreen(val game: Game?): ScreenDestination

    @Serializable
    data class SummaryScreen(val game: Game): ScreenDestination
}
