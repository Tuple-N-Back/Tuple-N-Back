package org.squidfish.tuplenback.di

import android.content.Context
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.squidfish.tuplenback.data.game.GameRepository
import org.squidfish.tuplenback.data.game.levels.LevelRepository
import org.squidfish.tuplenback.data.room.AppDatabase

@Module
class RepositoryModule {

    @Single
    fun createGameRepository(db: AppDatabase): GameRepository = GameRepository(db)

    @Single
    fun createLevelRepository(context: Context): LevelRepository = LevelRepository(context)
}
