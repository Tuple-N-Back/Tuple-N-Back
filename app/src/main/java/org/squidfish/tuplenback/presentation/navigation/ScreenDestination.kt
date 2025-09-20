package org.squidfish.tuplenback.presentation.navigation

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.reflect.typeOf
import org.squidfish.tuplenback.games.Game

sealed interface ScreenDestination {
    @Serializable
    data object MainScreen: ScreenDestination

    @Serializable
    data object GameSelectionScreen: ScreenDestination

    // FIXME: game is temporarily nullable until passing data between screens is reworked
    @Serializable
    data class GameScreen(val game: Game?, val level: Int): ScreenDestination

    @Serializable
    data class SummaryScreen(val game: Game, val level: Int): ScreenDestination
}
