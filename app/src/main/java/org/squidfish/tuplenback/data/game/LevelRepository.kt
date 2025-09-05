package org.squidfish.tuplenback.data.game

import org.squidfish.tuplenback.games.GameSettings
import org.squidfish.tuplenback.models.SearchRepository
import org.squidfish.tuplenback.utils.Error
import org.squidfish.tuplenback.utils.Result

object LevelRepository : SearchRepository<GameSettings, LevelData> {

    // TODO: fetch level settings from level config file
    override suspend fun get(key: LevelData): Result<GameSettings?, Error> = Result.Success(
        GameSettings(
            recallsBack = key.level,
            totalRounds = 3,
            milliPerRound = 1000,
            repeatChance = 50,
            timerUpdateInterval = 30,
        ),
    )

    override suspend fun insert(data: GameSettings): Result<Unit, Error> {
        TODO("Not yet implemented")
    }

    override suspend fun delete(data: GameSettings): Result<Unit, Error> {
        TODO("Not yet implemented")
    }
}
