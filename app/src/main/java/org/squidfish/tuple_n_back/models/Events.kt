package org.squidfish.tuple_n_back.models

sealed class AppEvent {
    data object PlayAgain : AppEvent()
    data object ResetGameState : AppEvent()
    data object AbortOngoingGame: AppEvent()
    data class StartGame(val game: Game) : AppEvent()
    data class MakeMnemonicRepeatGuess(val game: GameType) : AppEvent()
}