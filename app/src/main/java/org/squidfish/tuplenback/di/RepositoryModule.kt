package org.squidfish.tuplenback.di

import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.squidfish.tuplenback.data.LocalStorageRepository
import org.squidfish.tuplenback.data.room.AppDatabase

@Module
class RepositoryModule {

    @Single
    fun createRepository(db: AppDatabase): LocalStorageRepository = LocalStorageRepository(db)
}
