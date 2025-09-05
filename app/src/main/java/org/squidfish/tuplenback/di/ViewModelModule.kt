package org.squidfish.tuplenback.di

import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Module
import org.squidfish.tuplenback.data.game.GameRepository
import org.squidfish.tuplenback.models.GameViewModel

@Module
class ViewModelModule {

    @KoinViewModel
    fun createGameViewModel(repository: GameRepository): GameViewModel = GameViewModel(repository)
}
