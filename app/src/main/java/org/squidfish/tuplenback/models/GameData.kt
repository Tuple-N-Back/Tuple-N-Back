package org.squidfish.tuplenback.models

import org.squidfish.tuplenback.games.Game
import org.squidfish.tuplenback.games.GameModule
import org.squidfish.tuplenback.games.GameSettings

data class GameData(
    val gameType: Game,
    val gameSettings: GameSettings,
    val playerStats: Map<GameModule, PlayerPerformanceStats>,
    val gameEndTime: Long
)
