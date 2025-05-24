package org.squidfish.tuplenback.data.model

import org.junit.Test
import org.squidfish.tuplenback.data.GameStatsMapper
import org.squidfish.tuplenback.data.room.GameStatsData
import org.squidfish.tuplenback.games.Game
import org.squidfish.tuplenback.models.GameStatsModel

class GameStatsMapperTests {

    @Test
    fun convertGameStatsDataToGameStatsModel() {
        // Given
        val dataStats = GameStatsData(
            gameEndTime = 1,
            gameType = Game.GridPiano,
            difficulty = 3,
            correctRecalls = 4,
            incorrectRecalls = 5,
            missedRecalls = 6,
            gameModule = TODO(),
        )

        // When
        val modelStats = GameStatsMapper.Companion.toModel(dataStats)

        // Then
        assert(
            modelStats == GameStatsModel(
                gameEndTime = 1,
                gameType = Game.GridPiano,
                difficulty = 3,
                correctRecalls = 4,
                incorrectRecalls = 5,
                missedRecalls = 6,
            ),
        )
    }

    @Test
    fun convertGameStatsModelToGameStatsData() {
        // Given
        val modelStats = GameStatsModel(
            gameEndTime = 1,
            gameType = Game.GridPiano,
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
                difficulty = 3,
                correctRecalls = 4,
                incorrectRecalls = 5,
                missedRecalls = 6,
                gameModule = TODO(),
            ),
        )
    }
}
