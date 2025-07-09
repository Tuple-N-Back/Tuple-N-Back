package org.squidfish.tuplenback.di

import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.squidfish.tuplenback.data.game.GameRepository
import org.squidfish.tuplenback.data.room.AppDatabase

@Module
class RepositoryModule {

    @Single
    fun createRepository(db: AppDatabase): GameRepository = GameRepository(db)
}
