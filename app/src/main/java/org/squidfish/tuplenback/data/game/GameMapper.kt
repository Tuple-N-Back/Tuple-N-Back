package org.squidfish.tuplenback.data.game

import org.squidfish.tuplenback.data.room.GameStatsData
import org.squidfish.tuplenback.games.GameModule
import org.squidfish.tuplenback.models.GameModel
import org.squidfish.tuplenback.models.PlayerPerformanceStats
import org.squidfish.tuplenback.utils.Error
import org.squidfish.tuplenback.utils.InconsistentGameData
import org.squidfish.tuplenback.utils.InconsistentGameStats
import org.squidfish.tuplenback.utils.MissingGameStats
import org.squidfish.tuplenback.utils.Result

val List<GameStatsData>.asGameModel: Result<GameModel, Error>
    get() {
        val first = firstOrNull() ?: return Result.Error(MissingGameStats)

        forEach { entry ->
            if (
                entry.gameType != first.gameType ||
                entry.gameEndTime != first.gameEndTime ||
                entry.level != first.level
            ) {
                return Result.Error(InconsistentGameStats)
            }
        }

        val playerStats = mutableMapOf<GameModule, PlayerPerformanceStats>()
        forEach { stat ->
            playerStats[stat.gameModule] = PlayerPerformanceStats(
                correctRecalls = stat.correctRecalls,
                incorrectRecalls = stat.incorrectRecalls,
                missedRecalls = stat.missedRecalls,
                correctNonRecalls = stat.correctNonRecalls,
            )
        }

        val gameSettings = when (val res = LevelData(first.gameType, first.level).asGameSettings) {
            is Result.Error -> return res
            is Result.Success -> res.data
        }

        return Result.Success(
            GameModel(
                gameType = first.gameType,
                playerStats = playerStats,
                gameSettings = gameSettings,
                gameEndTime = first.gameEndTime,
            ),
        )
    }

val GameModel.asGameStatsData: Result<List<GameStatsData>, Error>
    get() {
        if (gameType.modules.size != playerStats.size) {
            return Result.Error(InconsistentGameData)
        }

        val gameStats = gameType.modules.map { module ->
            GameStatsData(
                gameType = gameType,
                gameModule = module,
                level = level,
                correctRecalls = playerStats[module]?.correctRecalls ?: return Result.Error(InconsistentGameData),
                incorrectRecalls = playerStats[module]?.incorrectRecalls ?: return Result.Error(InconsistentGameData),
                missedRecalls = playerStats[module]?.missedRecalls ?: return Result.Error(InconsistentGameData),
                correctNonRecalls = playerStats[module]?.correctNonRecalls ?: return Result.Error(
                    InconsistentGameData,
                ),
                gameEndTime = gameEndTime,
            )
        }

        return Result.Success(gameStats)
    }
