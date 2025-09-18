package org.squidfish.tuplenback.presentation.navigation

import android.os.Parcelable
import kotlinx.serialization.Serializable
import org.squidfish.tuplenback.data.game.levels.LevelData
import org.squidfish.tuplenback.games.Game
import org.squidfish.tuplenback.games.GameSettings
import org.squidfish.tuplenback.games.Level

sealed interface ScreenDestination {
    @Serializable
    data object MainScreen: ScreenDestination

    @Serializable
    data object GameSelectionScreen: ScreenDestination

    // FIXME: game is temporarily nullable until passing data between screens is reworked
    @Serializable
    data class GameScreen(val level: LevelData?): ScreenDestination

    @Serializable
    data class SummaryScreen(val level: LevelData?): ScreenDestination
}
