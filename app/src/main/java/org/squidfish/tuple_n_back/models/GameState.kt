package org.squidfish.tuple_n_back.models

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.platform.ComposeView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

data class GameState(
    val roundProgress: Float = 0f,
    val currentRound: Int = 0,
    val mnemonicIds: SnapshotStateMap<GameType, Int> = mutableStateMapOf(),
    val recallCheck: SnapshotStateMap<GameType, RecallCheck> = mutableStateMapOf(),
    val gameStats: SnapshotStateMap<GameType, GameStats> = mutableStateMapOf(),
    val gameOver: Boolean = false,
)

enum class RecallCheck {
    CORRECT,
    INCORRECT,
    NONE,
}