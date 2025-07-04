package org.squidfish.tuplenback.models

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import org.squidfish.tuplenback.games.Game
import org.squidfish.tuplenback.games.GameModule
import org.squidfish.tuplenback.models.PlayerPerformanceStats

/**
 * State of the ongoing game.
 *
 * @property[roundProgress] Percent of the round that has passed.
 * @property[currentRound] The current round the game is on.
 * @property[mnemonicIds] The recent-most generated mnemonics for each [GameModule].
 * @property[recallCheck] The guesses a user has made this round for each [GameModule].
 * @property[gameStatsModel] The stats for each [GameModule].
 * @property[gameOver] flag that signals that the game is over.
 *
 * TODO: mnemonicIds, recallCheck and gameStats should be wrapped in one object, maybe a GameData
 *  dataclass. Then, we only need to store a list of those GameData objects.
 * TODO: it makes no sense for gameStats to be here. It is not updated while the game is played and
 *  none of its information is displayed here. It's only here, because the same state is reused for
 *  the summary screen, but that also makes no sense, since it only utilizes the gameStats out of
 *  the state
 */
data class GameState(
    val roundProgress: Float = 0f,
    val currentRound: Int = 0,
    val mnemonicIds: SnapshotStateMap<GameModule, Int> = mutableStateMapOf(),
    val recallCheck: SnapshotStateMap<GameModule, RecallCheck> = mutableStateMapOf(),
    val playerPerformance: SnapshotStateMap<GameModule, PlayerPerformanceStats> = mutableStateMapOf(),
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
