package org.squidfish.tuple_n_back.models

import androidx.annotation.StringRes
import org.squidfish.tuple_n_back.R

data class GameSettings (
    val recallsBack: Int,
    val totalRounds: Int,
    val milliPerRound: Int
)

enum class GameType(@StringRes val type: Int) {
    Grid(R.string.grid_game),
    Sound(R.string.sound_game);

    fun toModel(recallsBack: Int): GameViewModel {
        return when (this) {
            Grid -> GridViewModel(recallsBack)
            Sound -> SoundViewModel(recallsBack)
        }
    }
}

class GameModel(private var settings: GameSettings = GameSettings(2,25,1500)) {

    private var games: List<GameViewModel> = listOf<GameViewModel>()
    private var stats: List<GameStats> = listOf<GameStats>()

    fun setGames(gameList: List<GameType>) {
        games = gameList.map { it.toModel(settings.recallsBack) }
    }

    fun getGames(): List<GameViewModel> {
        return games
    }

    fun setSettings(gameSettings: GameSettings) {
        settings = gameSettings
    }

    fun getSettings(): GameSettings {
        return settings
    }

    fun setStats(gameStats: List<GameStats>)  {
        stats = gameStats
    }

    fun getStats(): List<GameStats> {
        return stats
    }
}