package org.squidfish.tuplenback.di

import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Module
import org.squidfish.tuplenback.data.game.LocalStorageRepository
import org.squidfish.tuplenback.models.GameViewModel

@Module
class ViewModelModule {

    @KoinViewModel
    fun createGameViewModel(repository: LocalStorageRepository): GameViewModel = GameViewModel(repository)
}
