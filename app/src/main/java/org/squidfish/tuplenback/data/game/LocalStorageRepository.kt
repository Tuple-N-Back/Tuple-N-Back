package org.squidfish.tuplenback.data.game

import org.squidfish.tuplenback.data.room.AppDatabase
import org.squidfish.tuplenback.models.GameModel
import org.squidfish.tuplenback.models.Repository
import org.squidfish.tuplenback.utils.Error
import org.squidfish.tuplenback.utils.Result

/**
 * Provides access to the Room database
 */
class LocalStorageRepository(private val db: AppDatabase) : Repository {

    suspend fun getRecent(): Result<GameModel?, Error> {
        val recent = db.getGameStatsDao().getRecent(1)
        if (recent.isEmpty()) {
            return Result.Success(null)
        }

        return recent.asGameModel()
    }

    suspend fun insert(gameModel: GameModel): Result<Unit, Error> {
        when (val stats = gameModel.asGameStatsData()) {
            is Result.Success -> {
                db.getGameStatsDao().insertAll(stats.data)
                return Result.Success(Unit)
            }
            is Result.Error -> {
                return stats
            }
        }
    }

    suspend fun delete(gameModel: GameModel): Result<Unit, Error> {
        when (val stats = gameModel.asGameStatsData()) {
            is Result.Success -> {
                db.getGameStatsDao().deleteAll(stats.data)
                return Result.Success(Unit)
            }
            is Result.Error -> {
                return stats
            }
        }
    }
}
