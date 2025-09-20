package org.squidfish.tuplenback.models

import org.squidfish.tuplenback.games.Game
import org.squidfish.tuplenback.games.GameModule
import org.squidfish.tuplenback.games.GameSettings

/**
 * All the relevant information about a concluded game.
 *
 * @property[gameType] Type of [Game] played.
 * @property[level] Game difficulty level.
 * @property[gameSettings] [GameSettings] applicable to all [GameModule]s in the game.
 * @property[playerStats] Player performance for each [GameModule].
 * @property[gameEndTime] The time of a game's conclusion. Measured in POSIX time.
 *
 * TODO: Remove gameSettings when LevelRepository becomes available
 */
data class GameModel(
    val gameType: Game,
    val level: Int,
    val playerStats: Map<GameModule, PlayerPerformanceStats>,
    val gameEndTime: Long,
)
