package org.squidfish.tuplenback.data.game

import org.squidfish.tuplenback.games.Game
import org.squidfish.tuplenback.games.GameSettings
import org.squidfish.tuplenback.models.Repository

object LevelRepository : Repository {

    // TODO
    fun getSettings(game: Game, level: Int): GameSettings = GameSettings(
        recallsBack = level,
        totalRounds = 3,
        milliPerRound = 1000,
        repeatChance = 50,
        timerUpdateInterval = 30,
    )
}
