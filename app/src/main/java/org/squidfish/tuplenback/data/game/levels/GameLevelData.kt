package org.squidfish.tuplenback.data.game.levels

import kotlinx.serialization.Serializable

@Serializable
data class GameLevelData(val gameType: String, val levels: List<GameLevel>)

@Serializable
data class GameLevel(val levelId: Int, val settings: GameLevelSettings)

@Serializable
data class GameLevelSettings(
    val recallsBack: Int,
    val totalRounds: Int,
    val millisPerRound: Long,
    val repeatChance: Int,
)
