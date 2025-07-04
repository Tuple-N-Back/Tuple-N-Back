package org.squidfish.tuplenback.data

import org.koin.core.annotation.Single
import org.squidfish.tuplenback.data.room.AppDatabase
import org.squidfish.tuplenback.models.GameStatsModel

@Single
class LocalStorageRepository(val db: AppDatabase) {
    suspend fun getRecent(): GameStatsModel? = db.getGameStatsDao().getRecent(1).firstOrNull()?.let {
        GameStatsMapper.toModel(it)
    }

    suspend fun insert(stats: GameStatsModel) = db.getGameStatsDao().insert(GameStatsMapper.toData(stats))
    suspend fun delete(stats: GameStatsModel) = db.getGameStatsDao().delete(GameStatsMapper.toData(stats))
}
