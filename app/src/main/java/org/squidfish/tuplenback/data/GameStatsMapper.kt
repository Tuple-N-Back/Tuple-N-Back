package org.squidfish.tuplenback.data

import java.lang.IllegalArgumentException
import org.squidfish.tuplenback.data.room.GameStatsData
import org.squidfish.tuplenback.models.GameStatsModel

class GameStatsMapper {
    companion object {
        fun toData(stats: GameStatsModel): GameStatsData = GameStatsData(
            gameEndTime = stats.gameEndTime,
            gameType = stats.gameType ?: throw IllegalArgumentException("Game type is unset"),
            gameModule = stats.gameModule ?: throw IllegalArgumentException("Game module is unset"),
            difficulty = stats.difficulty,
            correctRecalls = stats.correctRecalls,
            incorrectRecalls = stats.incorrectRecalls,
            missedRecalls = stats.missedRecalls,
        )

        fun toModel(stats: GameStatsData): GameStatsModel = GameStatsModel(
            gameEndTime = stats.gameEndTime,
            gameType = stats.gameType,
            gameModule = stats.gameModule,
            difficulty = stats.difficulty,
            correctRecalls = stats.correctRecalls,
            incorrectRecalls = stats.incorrectRecalls,
            missedRecalls = stats.missedRecalls,
        )
    }
}
