package org.squidfish.tuplenback.data

import org.squidfish.tuplenback.data.room.AppDatabase
import org.squidfish.tuplenback.data.room.GameStats
import org.squidfish.tuplenback.data.Repository

class LocalStorageRepository(val db: AppDatabase) : Repository {
    fun getRecent(): GameStatsEntry? = db.getGameStatsDao().getRecent(1).firstOrNull()

    fun insert(stats: GameStatsEntry) = db.getGameStatsDao().insert(stats as GameStats)
    fun delete(stats: GameStatsEntry) = db.getGameStatsDao().delete(stats as GameStats)
}
