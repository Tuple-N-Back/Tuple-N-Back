package org.squidfish.tuplenback.data.game.levels

import org.squidfish.tuplenback.games.GameSettings
import org.squidfish.tuplenback.utils.Error
import org.squidfish.tuplenback.utils.LevelDeserializationError
import org.squidfish.tuplenback.utils.Result

val GameLevel.asGameSettings: Result<GameSettings, Error>
    get() = when {
        settings.recallsBack < 1 -> Result.Error(LevelDeserializationError.InvalidRecallsBack)
        settings.totalRounds <= settings.recallsBack -> Result.Error(LevelDeserializationError.TooFewRounds)
        settings.millisPerRound <= 0 -> Result.Error(LevelDeserializationError.InvalidRoundTime)
        settings.repeatChance !in 0..100 -> Result.Error(LevelDeserializationError.InvalidRepeatChance)

        else -> Result.Success(
            GameSettings(
                recallsBack = settings.recallsBack,
                totalRounds = settings.totalRounds,
                millisPerRound = settings.millisPerRound,
                repeatChance = settings.repeatChance,
            ),
        )
    }
