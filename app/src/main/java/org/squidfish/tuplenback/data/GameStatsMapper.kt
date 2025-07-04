package org.squidfish.tuplenback.data

import org.squidfish.tuplenback.data.room.GameStatsData
import org.squidfish.tuplenback.games.GameModule
import org.squidfish.tuplenback.games.GameSettings
import org.squidfish.tuplenback.models.GameData
import org.squidfish.tuplenback.models.PlayerPerformanceStats

class GameStatsMapper {
    object mapper {
        fun toModel(stats: List<GameStatsData>): GameData {

            val totalRounds = stats.first().correctRecalls + stats.first().incorrectRecalls + stats.first().missedRecalls + stats.first().mnemonicRepeatChance

            val settings = GameSettings(
                recallsBack = stats.first().difficulty,
                totalRounds = totalRounds,
                milliPerRound = stats.first().millisecondsPerRound,
                repeatChance = stats.first().mnemonicRepeatChance,
            )

            val playerStats = mutableMapOf<GameModule, PlayerPerformanceStats>()
            stats.forEach { stat ->
                playerStats[stat.gameModule] = PlayerPerformanceStats(
                    correctRecalls = stat.correctRecalls,
                    incorrectRecalls = stat.incorrectRecalls,
                    missedRecalls = stat.missedRecalls,
                    correctNonRecalls = stat.correctNonRecalls,
                )
            }

            return GameData(
                gameType = stats.first().gameType,
                playerStats = playerStats,
                gameSettings = settings,
                gameEndTime = stats.first().gameEndTime,
            )
        }

        fun toData(stats: GameData): List<GameStatsData> {
            return stats.gameType.modules.map { module ->
                GameStatsData(
                    gameType = stats.gameType,
                    gameModule = module,
                    difficulty = stats.gameType.settings.repeatChance,
                    millisecondsPerRound = stats.gameSettings.milliPerRound,
                    mnemonicRepeatChance = stats.gameSettings.repeatChance,
                    correctRecalls = stats.playerStats[module]?.correctRecalls ?: throw IllegalArgumentException(),
                    incorrectRecalls = stats.playerStats[module]?.incorrectRecalls ?: throw IllegalArgumentException(),
                    missedRecalls = stats.playerStats[module]?.missedRecalls ?: throw IllegalArgumentException(),
                    correctNonRecalls = stats.playerStats[module]?.correctNonRecalls ?: throw IllegalArgumentException(),
                    gameEndTime = stats.gameEndTime,
                )
            }
        }
    }
}
