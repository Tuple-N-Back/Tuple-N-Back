package org.squidfish.tuplenback.models

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import org.squidfish.tuplenback.games.GameModule
import org.squidfish.tuplenback.models.RecallCheck.NONE

/**
 * State of the ongoing game.
 *
 * @property[roundProgress] Percent of the round that has passed.
 * @property[currentRound] The current round the game is on.
 * @property[mnemonicIds] The recent-most generated mnemonics for each [GameModule].
 * @property[recallCheck] The guesses a user has made this round for each [GameModule].
 * @property[gameOver] flag that signals that the game is over.
 * @property[gameEndTime] The date of a game's conclusion Measured in POSIX time
 *
 */
data class GameState(
    val roundProgress: Float = 0f,
    val currentRound: Int = 0,
    val mnemonicIds: SnapshotStateMap<GameModule, Int> = mutableStateMapOf(),
    val recallCheck: SnapshotStateMap<GameModule, RecallCheck> = mutableStateMapOf(),
    val gameOver: Boolean = false,
    val gameEndTime: Long = System.currentTimeMillis(),
)

/**
 * The type of guess the player has made. No guess [NONE] is a valid state as well
 */
enum class RecallCheck {
    CORRECT,
    INCORRECT,
    NONE,
}
