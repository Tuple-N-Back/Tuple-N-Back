package org.squidfish.tuplenback.data.model.mapper

import kotlin.test.Test
import org.squidfish.tuplenback.data.game.levels.GameLevel
import org.squidfish.tuplenback.data.game.levels.GameLevelSettings
import org.squidfish.tuplenback.data.game.levels.asGameSettings
import org.squidfish.tuplenback.games.GameSettings
import org.squidfish.tuplenback.utils.LevelDeserializationError
import org.squidfish.tuplenback.utils.Result
import org.squidfish.tuplenback.utils.data

class GameLevelMapperTests {

    @Test
    fun `asGameSettings converts valid GameLevel to GameSettings correctly`() {
        // Given
        val gameLevel = GameLevel(
            levelId = 1,
            settings = GameLevelSettings(
                recallsBack = 2,
                totalRounds = 3,
                millisPerRound = 1000L,
                repeatChance = 50,
            ),
        )

        // When
        val result = gameLevel.asGameSettings

        // Then
        assert(
            result.data == GameSettings(
                recallsBack = 2,
                totalRounds = 3,
                millisPerRound = 1000L,
                repeatChance = 50,
            ),
        )
    }

    @Test
    fun `asGameSettings returns Error when recallsBack is less than 1`() {
        // Given
        val gameLevel = GameLevel(
            levelId = 1,
            settings = GameLevelSettings(
                recallsBack = 0, // Must be > 0
                totalRounds = 5,
                millisPerRound = 1000L,
                repeatChance = 50,
            ),
        )

        // When
        val result = gameLevel.asGameSettings

        // Then
        assert(result == Result.Error(LevelDeserializationError.InvalidRecallsBack))
    }

    @Test
    fun `asGameSettings returns Error when totalRounds is less than or equal to recallsBack`() {
        // Given
        val gameLevel = GameLevel(
            levelId = 1,
            settings = GameLevelSettings(
                recallsBack = 3,
                totalRounds = 3, // Must be > recallsBack
                millisPerRound = 1000L,
                repeatChance = 50,
            ),
        )

        // When
        val result = gameLevel.asGameSettings

        // Then
        assert(result == Result.Error(LevelDeserializationError.TooFewRounds))
    }

    @Test
    fun `asGameSettings returns Error when millisPerRound is less than or equal to 0`() {
        // Given
        val gameLevel = GameLevel(
            levelId = 1,
            settings = GameLevelSettings(
                recallsBack = 2,
                totalRounds = 5,
                millisPerRound = 0L, // Must be > 0
                repeatChance = 50,
            ),
        )

        // When
        val result = gameLevel.asGameSettings

        // Then
        assert(result == Result.Error(LevelDeserializationError.InvalidRoundTime))
    }

    @Test
    fun `asGameSettings returns Error when repeatChance is less than 0`() {
        // Given
        val gameLevel = GameLevel(
            levelId = 1,
            settings = GameLevelSettings(
                recallsBack = 2,
                totalRounds = 5,
                millisPerRound = 1000L,
                repeatChance = -1, // Must be >= 0 && <= 100
            ),
        )

        // When
        val result = gameLevel.asGameSettings

        // Then
        assert(result == Result.Error(LevelDeserializationError.InvalidRepeatChance))
    }

    @Test
    fun `asGameSettings returns Error when repeatChance is greater than 100`() {
        // Given
        val gameLevel = GameLevel(
            levelId = 1,
            settings = GameLevelSettings(
                recallsBack = 2,
                totalRounds = 5,
                millisPerRound = 1000L,
                repeatChance = -1, // Must be >= 0 && <= 100
            ),
        )

        // When
        val result = gameLevel.asGameSettings

        // Then
        assert(result == Result.Error(LevelDeserializationError.InvalidRepeatChance))
    }

    @Test
    fun `asGameSettings returns correct GameSettings with minimal boundary values`() {
        // Given
        val gameLevel = GameLevel(
            levelId = 1,
            settings = GameLevelSettings(
                recallsBack = 1, // Minimum valid value
                totalRounds = 2, // Minimum valid value (recallsBack + 1)
                millisPerRound = 1L, // Minimum valid value
                repeatChance = 0, // Minimum valid value
            ),
        )

        // When
        val result = gameLevel.asGameSettings

        // Then
        assert(
            result.data == GameSettings(
                recallsBack = 1,
                totalRounds = 2,
                millisPerRound = 1L,
                repeatChance = 0,
            ),
        )
    }
}
