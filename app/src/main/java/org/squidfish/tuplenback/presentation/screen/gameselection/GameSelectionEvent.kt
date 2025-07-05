package org.squidfish.tuplenback.presentation.screen.gameselection

sealed interface GameSelectionEvent {
    data class StartGame(val game: GameModel) : GameSelectionEvent
}
