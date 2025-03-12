package org.squidfish.tuple_n_back.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import org.squidfish.tuple_n_back.R
import org.squidfish.tuple_n_back.games.GridGame

data class GameSettings (
    val recallsBack: Int,
    val totalRounds: Int,
    val milliPerRound: Long,
    val repeatChance: Int,
    val timerUpdateInterval: Long = 50, // in millis
)

enum class GameType(@StringRes val type: Int) {
    Grid(R.string.grid_game),
    Piano(R.string.sound_game),
    Colour(3),
    Vibration(4);

    fun toGameEngine(recallsBack: Int, repeatChance: Int): GameEngine {
        return when (this) {
            Grid -> GridEngine(recallsBack, repeatChance)
            Piano -> SoundGameEngine(recallsBack, repeatChance)
            Colour -> TODO()
            Vibration -> TODO()
        }
    }

    fun toGameIconRes(): Int {
        return when (this) {
            Grid -> R.drawable.grid_game_icon
            Piano -> R.drawable.piano_game_icon

            else -> TODO()
        }
    }
}

enum class Game(val modules: List<GameType>, val settings: GameSettings) {
    None(listOf(), GameSettings(-1,-1,-1,-1)),
    GridPiano(listOf(GameType.Grid, GameType.Piano), GameSettings(2, 3, 1500, 15)),
    Grid(listOf(GameType.Grid), GameSettings(2, 3, 1500, 20 ))
}