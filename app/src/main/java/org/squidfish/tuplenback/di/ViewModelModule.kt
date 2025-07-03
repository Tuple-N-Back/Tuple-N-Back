package org.squidfish.tuplenback.di

import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.Module
import org.squidfish.tuplenback.models.GameViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import org.squidfish.tuplenback.data.Repository

@Module
class ViewModelModule {

    @KoinViewModel
    fun createGameViewModel(repository: Repository): GameViewModel = GameViewModel(repository)
}
