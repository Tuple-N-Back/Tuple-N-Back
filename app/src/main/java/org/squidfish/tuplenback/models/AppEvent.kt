package org.squidfish.tuplenback.models

import org.squidfish.tuplenback.games.Game
import org.squidfish.tuplenback.games.GameModule

/**
 * Events used to signal the view model
 */
sealed interface AppEvent {
    /**Start a game of the [Game] type that was most recently played.*/
    data object PlayAgain : AppEvent

    /**Clear the [GameState].*/
    data object ResetGameState: AppEvent

    /**Terminate a game before its end.*/
    data object AbortOngoingGame: AppEvent

    /**
     * Start a new game.
     *
     * @param[game] the [Game] to start
     */
    data class StartGame(val game: Game): AppEvent

    /**
     * Mak a guess, suggesting a mnemonic repeat happened for a game module.
     *
     * @param[gameMod] the game module that is guessed on.
     */
    data class MakeMnemonicRepeatGuess(val gameMod: GameModule): AppEvent
}
