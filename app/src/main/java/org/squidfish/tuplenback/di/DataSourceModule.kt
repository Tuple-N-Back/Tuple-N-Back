package org.squidfish.tuplenback.di

import android.content.Context
import androidx.room.Room
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.squidfish.tuplenback.data.room.AppDatabase

@Module
class DataSourceModule {

    @Single
    fun createAppDatabase(context: Context): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "app-database",
    ).fallbackToDestructiveMigration(true).build()
}
