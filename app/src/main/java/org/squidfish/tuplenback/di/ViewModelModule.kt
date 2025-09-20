package org.squidfish.tuplenback.di

import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Module
import org.squidfish.tuplenback.data.game.GameRepository
import org.squidfish.tuplenback.data.game.levels.LevelRepository
import org.squidfish.tuplenback.models.GameViewModel
import org.squidfish.tuplenback.presentation.screen.gameselection.GameSelectionViewModel

@Module
class ViewModelModule {
    // FIXME: ktlint disagrees with every other way to write this
    @KoinViewModel
    fun createGameViewModel(gameRepository: GameRepository, levelRepository: LevelRepository): GameViewModel =
        GameViewModel(gameRepository, levelRepository)

    @KoinViewModel
    fun createGameSelectionViewModel(): GameSelectionViewModel = GameSelectionViewModel()
}
