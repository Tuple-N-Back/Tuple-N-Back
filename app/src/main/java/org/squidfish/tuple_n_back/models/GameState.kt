package org.squidfish.tuple_n_back.models

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

data class GameState(
    val roundProgress: Float = 0f,
    val currentRound: Int = 0,
    val mnemonicIds: MutableMap<GameType, Int> = mutableMapOf(),
    val recallCheck: MutableMap<GameType, RecallCheck> = mutableMapOf(),
    val gameStats: MutableMap<GameType, GameStats> = mutableMapOf(),
    val gameOver: Boolean = false,
)

enum class RecallCheck {
    CORRECT,
    INCORRECT,
    NONE,
}