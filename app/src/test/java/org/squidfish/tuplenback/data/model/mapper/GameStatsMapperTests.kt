package org.squidfish.tuplenback.data.model.mapper

import org.junit.Test
import org.squidfish.tuplenback.data.game.asGameModel
import org.squidfish.tuplenback.data.room.GameStatsData
import org.squidfish.tuplenback.games.Game
import org.squidfish.tuplenback.games.GameModule
import org.squidfish.tuplenback.games.GameSettings
import org.squidfish.tuplenback.models.GameModel
import org.squidfish.tuplenback.models.PlayerPerformanceStats
import org.squidfish.tuplenback.utils.Result
import org.squidfish.tuplenback.utils.ValidationError
import org.squidfish.tuplenback.utils.data

class GameStatsMapperTests {

    @Test
    fun `asModel converts a list of GameStatsData to GameData`() {
        // Given
        val dataStats = listOf(
            GameStatsData(
                gameEndTime = 1,
                gameType = Game.GridPiano,
                gameModule = GameModule.Grid,
                level = 3,
                correctRecalls = 1,
                incorrectRecalls = 2,
                missedRecalls = 3,
                correctNonRecalls = 4,
            ),
            GameStatsData(
                gameEndTime = 1,
                gameType = Game.GridPiano,
                gameModule = GameModule.Piano,
                level = 3,
                correctRecalls = 5,
                incorrectRecalls = 6,
                missedRecalls = 7,
                correctNonRecalls = 8,
            ),
        )

        // When
        val modelStats = dataStats.asGameModel.data

        // Then
        assert(
            modelStats == GameModel(
                gameEndTime = 1,
                gameType = Game.GridPiano,
                level = 3,
                playerStats = mapOf(
                    GameModule.Grid to PlayerPerformanceStats(
                        correctRecalls = 1,
                        incorrectRecalls = 2,
                        missedRecalls = 3,
                        correctNonRecalls = 4,
                    ),
                    GameModule.Piano to PlayerPerformanceStats(
                        correctRecalls = 5,
                        incorrectRecalls = 6,
                        missedRecalls = 7,
                        correctNonRecalls = 8,
                    ),
                ),
            ),
        )
    }

    @Test
    fun `asModel returns an error when given an empty list of GameStatsData`() {
        // Given
        val dataStats = listOf<GameStatsData>()

        // When
        val modelStats = dataStats.asGameModel

        // Then
        assert(modelStats == Result.Error(ValidationError.MissingGameStats))
    }

    @Test
    fun `asModel returns an error when given a list of GameStatsData with conflicting values`() {
        // Given
        val dataStats = listOf(
            GameStatsData(
                gameEndTime = 1,
                gameType = Game.GridPiano,
                gameModule = GameModule.Grid,
                level = 3,
                correctRecalls = 1,
                incorrectRecalls = 2,
                missedRecalls = 3,
                correctNonRecalls = 4,
            ),
            GameStatsData(
                gameEndTime = 2,
                gameType = Game.Grid,
                gameModule = GameModule.Grid,
                level = 4,
                correctRecalls = 5,
                incorrectRecalls = 6,
                missedRecalls = 7,
                correctNonRecalls = 8,
            ),
        )

        // When
        val modelStats = dataStats.asGameModel

        // Then
        assert(
            modelStats == Result.Error(ValidationError.InconsistentGameStats),
        )
    }
}
