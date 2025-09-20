package org.squidfish.tuplenback.presentation.screen.gameselection

import androidx.annotation.DrawableRes

data class GameSelectionState(val games: List<GameModel> = emptyList())

data class GameModel(
    val title: String = "",
    @field:DrawableRes val icons: List<Int> = emptyList(),
    val highestLevel: String = "",
    val totalGames: String = "",
    val currentLevel: Int = 1,
)
