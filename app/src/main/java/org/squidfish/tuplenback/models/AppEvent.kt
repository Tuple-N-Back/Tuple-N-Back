package org.squidfish.tuplenback.models

import org.squidfish.tuplenback.data.game.levels.LevelData
import org.squidfish.tuplenback.games.Game
import org.squidfish.tuplenback.games.GameModule
import org.squidfish.tuplenback.games.GameSettings
import org.squidfish.tuplenback.games.Level

/**
 * Events used to signal the view model
 */
sealed interface AppEvent {
    /**Start a game of the [Game] type that was most recently played.*/
    data object PlayAgain : AppEvent

    /**Clear the [GameState].*/
    data object ResetGameState : AppEvent

    /**Terminate a game before its end.*/
    data object AbortOngoingGame : AppEvent

    /**
     * Start a new game.
     *
     * @param[level] the [Level] to start
     */
    data class StartGame(val level: LevelData) : AppEvent

    /**
     * Mak a guess, suggesting a mnemonic repeat happened for a game module.
     *
     * @param[gameMod] the game module that is guessed on.
     */
    data class MakeMnemonicRepeatGuess(val gameMod: GameModule) : AppEvent

    data object FinishGame : AppEvent
}
