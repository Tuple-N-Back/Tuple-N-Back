package org.squidfish.tuplenback.di

import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Module
import org.squidfish.tuplenback.models.GameViewModel
import org.squidfish.tuplenback.presentation.screen.gameselection.GameSelectionViewModel

@Module
class ViewModelModule {
    @KoinViewModel
    fun createGameViewModel(): GameViewModel = GameViewModel()

    @KoinViewModel
    fun createGameSelectionViewModel(): GameSelectionViewModel = GameSelectionViewModel()
}
