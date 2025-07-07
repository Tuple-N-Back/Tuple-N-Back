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

fun List<GameStatsData>.asGameModel(): Result<GameModel, Error> {
    if (this.isEmpty()) {
        return Result.Error(MissingGameStats)
    }

    val first = this.first()

    this.forEach { entry ->
        if (
            entry.gameType != first.gameType ||
            entry.gameEndTime != first.gameEndTime ||
            entry.level != first.level
        ) {
            return Result.Error(InconsistentGameStats)
        }
    }

    val playerStats = mutableMapOf<GameModule, PlayerPerformanceStats>()
    this.forEach { stat ->
        playerStats[stat.gameModule] = PlayerPerformanceStats(
            correctRecalls = stat.correctRecalls,
            incorrectRecalls = stat.incorrectRecalls,
            missedRecalls = stat.missedRecalls,
            correctNonRecalls = stat.correctNonRecalls,
        )
    }

    return Result.Success(
        GameModel(
            gameType = first.gameType,
            playerStats = playerStats,
            gameSettings = LevelRepository.getSettings(first.gameType, first.level),
            gameEndTime = first.gameEndTime,
        ),
    )
}

fun GameModel.asGameStatsData(): Result<List<GameStatsData>, Error> {
    if (this.gameType.modules.size != this.playerStats.size) {
        return Result.Error(InconsistentGameData)
    }

    val gameStats = this.gameType.modules.map { module ->
        GameStatsData(
            gameType = this.gameType,
            gameModule = module,
            level = this.level,
            correctRecalls = this.playerStats[module]?.correctRecalls ?: return Result.Error(InconsistentGameData),
            incorrectRecalls = this.playerStats[module]?.incorrectRecalls ?: return Result.Error(InconsistentGameData),
            missedRecalls = this.playerStats[module]?.missedRecalls ?: return Result.Error(InconsistentGameData),
            correctNonRecalls = this.playerStats[module]?.correctNonRecalls ?: return Result.Error(
                InconsistentGameData,
            ),
            gameEndTime = this.gameEndTime,
        )
    }

    return Result.Success(gameStats)
}
