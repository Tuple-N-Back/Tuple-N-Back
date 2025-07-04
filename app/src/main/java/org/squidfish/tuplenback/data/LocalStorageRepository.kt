package org.squidfish.tuplenback.data

import org.squidfish.tuplenback.data.room.AppDatabase
import org.squidfish.tuplenback.models.GameData

class LocalStorageRepository(private val db: AppDatabase) {

    suspend fun getRecent(): GameData? = GameStatsMapper.mapper.toModel(db.getGameStatsDao().getRecent(1))

    suspend fun insert(gameData: GameData) = db.getGameStatsDao().insertAll(GameStatsMapper.mapper.toData(gameData))
    suspend fun delete(gameData: GameData) = db.getGameStatsDao().deleteAll(GameStatsMapper.mapper.toData(gameData))
}
