package org.squidfish.tuplenback.data.model

import org.junit.Test
import org.squidfish.tuplenback.data.GameStatsMapper
import org.squidfish.tuplenback.data.room.GameStatsData
import org.squidfish.tuplenback.games.Game
import org.squidfish.tuplenback.games.GameModule
import org.squidfish.tuplenback.models.GameStatsModel

class GameStatsMapperTests {

    @Test
    fun `toModel converts GameStatsData to GameStatsModel`() {
        // Given
        val dataStats = GameStatsData(
            gameEndTime = 1,
            gameType = Game.GridPiano,
            gameModule = GameModule.Grid,
            difficulty = 3,
            correctRecalls = 4,
            incorrectRecalls = 5,
            missedRecalls = 6,
        )

        // When
        val modelStats = GameStatsMapper.Companion.toModel(dataStats)

        // Then
        assert(
            modelStats == GameStatsModel(
                gameEndTime = 1,
                gameType = Game.GridPiano,
                gameModule = GameModule.Grid,
                difficulty = 3,
                correctRecalls = 4,
                incorrectRecalls = 5,
                missedRecalls = 6,
            ),
        )
    }

    @Test
    fun `toData converts GameStatsModel to GameStatsData`() {
        // Given
        val modelStats = GameStatsModel(
            gameEndTime = 1,
            gameType = Game.GridPiano,
            gameModule = GameModule.Grid,
            difficulty = 3,
            correctRecalls = 4,
            incorrectRecalls = 5,
            missedRecalls = 6,
        )

        // When
        val dataStats = GameStatsMapper.Companion.toData(modelStats)

        // Then
        assert(
            dataStats == GameStatsData(
                gameEndTime = 1,
                gameType = Game.GridPiano,
                gameModule = GameModule.Grid,
                difficulty = 3,
                correctRecalls = 4,
                incorrectRecalls = 5,
                missedRecalls = 6,
            ),
        )
    }

    @Test
    fun `toData throws an error when given GameStatsModel with null gameModule`() {
        // Given
        val modelStats = GameStatsModel(
            gameEndTime = 1,
            gameType = Game.GridPiano,
            gameModule = null,
            difficulty = 3,
            correctRecalls = 4,
            incorrectRecalls = 5,
            missedRecalls = 6,
        )

        try {
            // When
            GameStatsMapper.Companion.toData(modelStats)
        } catch (exception: IllegalArgumentException) {
            assert(true)
        }
    }

    @Test
    fun `toData throws an error when given GameStatsModel with null gameType`() {
        // Given
        val modelStats = GameStatsModel(
            gameEndTime = 1,
            gameType = null,
            gameModule = GameModule.Grid,
            difficulty = 3,
            correctRecalls = 4,
            incorrectRecalls = 5,
            missedRecalls = 6,
        )

        try {
            // When
            GameStatsMapper.Companion.toData(modelStats)
        } catch (exception: IllegalArgumentException) {
            assert(true)
        }
    }
}
