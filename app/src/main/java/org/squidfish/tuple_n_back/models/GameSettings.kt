package org.squidfish.tuple_n_back.models

import androidx.annotation.StringRes
import org.squidfish.tuple_n_back.R

data class GameSettings (
    val recallsBack: Int,
    val totalRounds: Int,
    val milliPerRound: Long,
    val timerUpdateInterval: Long = 50, // in millis
    val games: List<GameType> = listOf()
)

enum class GameType(@StringRes val type: Int) {
    Grid(R.string.grid_game),
    Sound(R.string.sound_game);

    fun toGameEngine(recallsBack: Int): GameEngine {
        return when (this) {
            Grid -> GridEngine(recallsBack)
            Sound -> SoundGameEngine(recallsBack)
        }
    }
}