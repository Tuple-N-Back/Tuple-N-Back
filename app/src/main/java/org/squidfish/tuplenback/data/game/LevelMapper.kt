package org.squidfish.tuplenback.data.game

import org.squidfish.tuplenback.games.GameSettings
import org.squidfish.tuplenback.utils.Error
import org.squidfish.tuplenback.utils.Result

val LevelData.asGameSettings: Result<GameSettings, Error>
    get() {
        // TODO: probably have a preloaded map of level -> settings

        return Result.Success(
            GameSettings(
                recallsBack = level,
                totalRounds = 3,
                milliPerRound = 1000,
                repeatChance = 50,
                timerUpdateInterval = 30,
            ),
        )
    }
