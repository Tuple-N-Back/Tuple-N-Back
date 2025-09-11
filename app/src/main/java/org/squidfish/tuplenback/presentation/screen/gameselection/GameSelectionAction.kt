package org.squidfish.tuplenback.presentation.screen.gameselection

import org.squidfish.tuplenback.games.Game

sealed interface GameSelectionAction {
    /**
     * Start a new game.
     *
     * @param[game] the [Game] to start
     */
    data class GameSelected(val game: GameModel) : GameSelectionAction
}
