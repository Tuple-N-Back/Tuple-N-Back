package org.squidfish.tuplenback.data.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [GameStatsData::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getGameStatsDao(): GameStatsDao
}
