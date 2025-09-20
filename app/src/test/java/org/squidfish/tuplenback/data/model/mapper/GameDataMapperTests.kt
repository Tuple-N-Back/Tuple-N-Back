package org.squidfish.tuplenback.data.model.mapper

import org.junit.Test
import org.squidfish.tuplenback.data.game.asGameStatsData
import org.squidfish.tuplenback.data.room.GameStatsData
import org.squidfish.tuplenback.games.Game
import org.squidfish.tuplenback.games.GameModule
import org.squidfish.tuplenback.games.GameSettings
import org.squidfish.tuplenback.models.GameModel
import org.squidfish.tuplenback.models.PlayerPerformanceStats
import org.squidfish.tuplenback.utils.Result
import org.squidfish.tuplenback.utils.ValidationError
import org.squidfish.tuplenback.utils.data

class GameDataMapperTests {

    @Test
    fun `asData converts GameData to a list of GameStatsData`() {
        // Given
        val modelStats = GameModel(
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
        )

        // When
        val dataStats = modelStats.asGameStatsData.data

        // Then
        assert(
            dataStats == listOf(
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
            ),
        )
    }

    @Test
    fun `asData returns an error when given GameStatsModel with conflicting values`() {
        // Given
        val modelStats = GameModel(
            gameEndTime = 1,
            gameType = Game.Grid,
            level = 3,
            playerStats = mapOf(
                GameModule.Piano to PlayerPerformanceStats(
                    correctRecalls = 5,
                    incorrectRecalls = 6,
                    missedRecalls = 7,
                    correctNonRecalls = 8,
                ),
            ),
        )

        // When
        val dataStats = modelStats.asGameStatsData

        // Then
        assert(dataStats == Result.Error(ValidationError.InconsistentGameData))
    }
}
