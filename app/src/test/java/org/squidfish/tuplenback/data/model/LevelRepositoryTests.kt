package org.squidfish.tuplenback.data.model

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner
import org.squidfish.tuplenback.data.game.levels.LevelRepository
import org.squidfish.tuplenback.games.Game
import org.squidfish.tuplenback.games.GameSettings
import org.squidfish.tuplenback.presentation.navigation.LevelData
import org.squidfish.tuplenback.utils.LevelDeserializationError
import org.squidfish.tuplenback.utils.Result
import org.squidfish.tuplenback.utils.data

@RunWith(RobolectricTestRunner::class)
class LevelRepositoryTests {
    private lateinit var levelRepositoryForValidLevels : LevelRepository
    private lateinit var context : Application

    @Before
    fun createLevelRepository() {
        this.context = ApplicationProvider.getApplicationContext<Application>()
        levelRepositoryForValidLevels = LevelRepository(context, "test_levels/valid_levels")
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `load level of valid config`() = runTest {
        // When
        val settings = levelRepositoryForValidLevels.get(LevelData(Game.Grid, 0))

        // Then
        assert(
            settings.data == GameSettings(
                recallsBack = 2,
                totalRounds = 3,
                millisPerRound = 1000,
                repeatChance = 20,
            ),
        )
    }

    @Test
    fun `load level of valid config from cache`() = runTest {
        // Given
        levelRepositoryForValidLevels.get(LevelData(Game.Grid, 0))

        // When
        val settings = levelRepositoryForValidLevels.get(LevelData(Game.Grid, 0))

        // Then
        assert(
            settings.data == GameSettings(
                recallsBack = 2,
                totalRounds = 3,
                millisPerRound = 1000,
                repeatChance = 20,
            ),
        )
    }

    @Test
    fun `load non-existent level of valid config`() = runTest {
        // When
        val settings = levelRepositoryForValidLevels.get(LevelData(Game.Grid, 1000))

        // Then
        assert(settings == Result.Error(LevelDeserializationError.NonexistentLevel))
    }

    @Test
    fun `load non-existent level of valid config from cache`() = runTest {
        // Given
        levelRepositoryForValidLevels.get(LevelData(Game.Grid, 1000))

        // When
        val settings = levelRepositoryForValidLevels.get(LevelData(Game.Grid, 1000))

        // Then
        assert(settings == Result.Error(LevelDeserializationError.NonexistentLevel))
    }

    @Test
    fun `load settings of valid config for negative level`() = runTest {
        // When
        val settings = levelRepositoryForValidLevels.get(LevelData(Game.GridVibration, -1))

        // Then
        assert(settings == Result.Error(LevelDeserializationError.NonexistentLevel))
    }

    @Test
    fun `load settings of valid config for negative level from cache`() = runTest {
        // Given
        levelRepositoryForValidLevels.get(LevelData(Game.GridVibration, -1))

        // When
        val settings = levelRepositoryForValidLevels.get(LevelData(Game.GridVibration, -1))

        // Then
        assert(settings == Result.Error(LevelDeserializationError.NonexistentLevel))
    }

    @Test
    fun `load settings of game with a missing config file`() = runTest {
        // When
        val settings = levelRepositoryForValidLevels.get(LevelData(Game.GridVibration, 0))

        // Then
        assert(settings == Result.Error(LevelDeserializationError.MissingConfig))
    }

    @Test
    fun `get settings from cache initialized with valid configs`() = runTest {
        // Given
        levelRepositoryForValidLevels.initializeCache()

        // When
        val settings = levelRepositoryForValidLevels.getFromCache(LevelData(Game.Grid, 0))

        // Then
        assert(
            settings.data == GameSettings(
                recallsBack = 2,
                totalRounds = 3,
                millisPerRound = 1000,
                repeatChance = 20,
            ),
        )
    }

    @Test
    fun `get settings from non-initialized cache`() = runTest {
        // When
        val settings = levelRepositoryForValidLevels.getFromCache(LevelData(Game.Grid, 0))

        // Then
        assert(settings == Result.Error(LevelDeserializationError.IncompleteCache))
    }
}
