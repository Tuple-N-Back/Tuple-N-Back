package org.squidfish.tuplenback.data.game

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.squidfish.tuplenback.data.game.levels.LevelRepository
import org.squidfish.tuplenback.data.room.AppDatabase
import org.squidfish.tuplenback.models.GameModel
import org.squidfish.tuplenback.models.RecentRepository
import org.squidfish.tuplenback.utils.Error
import org.squidfish.tuplenback.utils.Result

/**
 * Provides access to the Room database
 */
class GameRepository(private val db: AppDatabase, val levelRepository: LevelRepository) : RecentRepository<GameModel> {

    override suspend fun getRecent(): Result<GameModel?, Error> {
        val recent = db.getGameStatsDao().getRecent(1)
        if (recent.isEmpty()) {
            return Result.Success(null)
        }

        return recent.toGameModel(levelRepository)
    }

    override suspend fun insert(data: GameModel): Result<Unit, Error> = when (val stats = data.asGameStatsData) {
        is Result.Success -> {
            db.getGameStatsDao().insertAll(stats.data)
            Result.Success(Unit)
        }
        is Result.Error -> stats
    }

    override suspend fun delete(data: GameModel): Result<Unit, Error> = when (val stats = data.asGameStatsData) {
        is Result.Success -> {
            db.getGameStatsDao().deleteAll(stats.data)
            Result.Success(Unit)
        }
        is Result.Error -> stats
    }
}
