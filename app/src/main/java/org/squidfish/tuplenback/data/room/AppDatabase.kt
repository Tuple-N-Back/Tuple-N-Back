package org.squidfish.tuplenback.data.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [GameStats::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getGameStatsDao(): GameStatsDao
}
