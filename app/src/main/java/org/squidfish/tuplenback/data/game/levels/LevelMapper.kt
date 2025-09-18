package org.squidfish.tuplenback.data.game.levels

import org.squidfish.tuplenback.games.GameSettings
import org.squidfish.tuplenback.games.Level
import org.squidfish.tuplenback.utils.Error
import org.squidfish.tuplenback.utils.LevelDeserializationError
import org.squidfish.tuplenback.utils.Result

val GameLevel.asGameSettings: Result<GameSettings, Error>
    get() {
        if (settings.recallsBack < 1) {
            return Result.Error(LevelDeserializationError.InvalidRecallsBack)
        }

        if (settings.totalRounds <= settings.recallsBack) {
            return Result.Error(LevelDeserializationError.TooFewRounds)
        }

        if (settings.millisPerRound <= 0) {
            return Result.Error(LevelDeserializationError.InvalidRoundTime)
        }

        if (settings.repeatChance < 0 || settings.repeatChance > 100) {
            return Result.Error(LevelDeserializationError.InvalidRepeatChance)
        }

        return Result.Success(
            GameSettings(
                recallsBack = settings.recallsBack,
                totalRounds = settings.totalRounds,
                millisPerRound = settings.millisPerRound,
                repeatChance = settings.repeatChance,
                timerUpdateInterval = 30,
            ),
        )
    }
