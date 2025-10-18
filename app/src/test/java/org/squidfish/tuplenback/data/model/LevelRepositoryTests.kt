package org.squidfish.tuplenback.data.model

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.spy
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
    private lateinit var realLevelRepository: LevelRepository
    private lateinit var spiedLevelRepository: LevelRepository
    private lateinit var context: Application

    val singleGridLevelConfig = """
        {
            "gameType": "Grid",
            "levels": [
                {
                    "levelId": 0,
                    "settings": {
                        "recallsBack": 2,
                        "totalRounds": 3,
                        "millisPerRound": 1000,
                        "repeatChance": 20
                    }
                }
            ]
        }
    """.trimIndent()

    @Before
    fun createLevelRepository() {
        this.context = ApplicationProvider.getApplicationContext<Application>()
        realLevelRepository = LevelRepository(context)
        spiedLevelRepository = spy(realLevelRepository)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `load level`() = runTest {
        // Given
        doReturn(Result.Success(singleGridLevelConfig)).`when`(spiedLevelRepository).readLevelData(Game.Grid)

        // When
        val settings = spiedLevelRepository.get(LevelData(Game.Grid, 0))

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
    fun `load level from cache`() = runTest {
        // Given
        doReturn(Result.Success(singleGridLevelConfig)).`when`(spiedLevelRepository).readLevelData(Game.Grid)
        spiedLevelRepository.get(LevelData(Game.Grid, 0))

        // When
        val settings = spiedLevelRepository.get(LevelData(Game.Grid, 0))

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
    fun `load nonexistent level`() = runTest {
        // Given
        doReturn(Result.Success(singleGridLevelConfig)).`when`(spiedLevelRepository).readLevelData(Game.Grid)

        // When
        val settings = spiedLevelRepository.get(LevelData(Game.Grid, 1))

        // Then
        assert(settings == Result.Error(LevelDeserializationError.NonexistentLevel))
    }

    @Test
    fun `error when loading settings for a negative level`() = runTest {
        // Given
        doReturn(Result.Success(singleGridLevelConfig)).`when`(spiedLevelRepository).readLevelData(Game.Grid)

        // When
        val settings = spiedLevelRepository.get(LevelData(Game.Grid, -1))

        // Then
        assert(settings == Result.Error(LevelDeserializationError.NonexistentLevel))
    }

    @Test
    fun `load settings from cache initialized repository`() = runTest {
        // Given
        doReturn(Result.Success(singleGridLevelConfig)).`when`(spiedLevelRepository).readLevelData(Game.Grid)

        spiedLevelRepository.initializeCache()

        // When
        val settings = spiedLevelRepository.get(LevelData(Game.Grid, 0))

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
    fun `error when loading level from invalid config that has an incorrect json syntax`() = runTest {
        // Given
        doReturn(
            Result.Success(
                """
                    {
                        "gameType": "Grid",
                        "levels": [
                        {
                            "levelId": 0,
                            "settings": {
                                "recallsBack": 2,
                                "totalRounds" 3,
                                "millisPerRound": 1000,
                                "repeatChance": 20
                            }
                    }]}
                """.trimIndent(),
            ),
        ).`when`(spiedLevelRepository).readLevelData(Game.Grid)

        // When
        val settings = spiedLevelRepository.get(LevelData(Game.Grid, 0))

        // Then
        assert(settings == Result.Error(LevelDeserializationError.InvalidSyntax))
    }

    @Test
    fun `error when loading level from invalid config that has incorrect json fields`() = runTest {
        // Given
        doReturn(
            Result.Success(
                """
                    {
                        "gameType": "Grid",
                        "levels": [
                        {
                            "levelId": 0,
                            "settings": {
                                "totalRounds": 3,
                                "millisPerRound": 1000,
                                "repeatChance": 20
                            }
                    }]}
                """.trimIndent(),
            ),
        ).`when`(spiedLevelRepository).readLevelData(Game.Grid)

        // When
        val settings = spiedLevelRepository.get(LevelData(Game.Grid, 0))

        // Then
        assert(settings == Result.Error(LevelDeserializationError.InvalidSyntax))
    }

    @Test
    fun `error when loading level from invalid config that has a too high recallsBack value`() = runTest {
        // Given
        doReturn(
            Result.Success(
                """
                    {
                        "gameType": "Grid",
                        "levels": [
                        {
                            "levelId": 0,
                            "settings": {
                                "recallsBack": 3,
                                "totalRounds": 3,
                                "millisPerRound": 1000,
                                "repeatChance": 20
                            }
                    }]}
                """.trimIndent(),
            ),
        ).`when`(spiedLevelRepository).readLevelData(Game.Grid)

        // When
        val settings = spiedLevelRepository.get(LevelData(Game.Grid, 0))

        // Then
        assert(settings == Result.Error(LevelDeserializationError.TooFewRounds))
    }

    @Test
    fun `error when loading level from invalid config that has a too small recallsBack value`() = runTest {
        // Given
        doReturn(
            Result.Success(
                """
                    {
                        "gameType": "Grid",
                        "levels": [
                        {
                            "levelId": 0,
                            "settings": {
                                "recallsBack": 0,
                                "totalRounds": 3,
                                "millisPerRound": 1000,
                                "repeatChance": 20
                            }
                    }]}
                """.trimIndent(),
            ),
        ).`when`(spiedLevelRepository).readLevelData(Game.Grid)

        // When
        val settings = spiedLevelRepository.get(LevelData(Game.Grid, 0))

        // Then
        assert(settings == Result.Error(LevelDeserializationError.InvalidRecallsBack))
    }

    @Test
    fun `error when loading level from invalid config that has a too small millisPerRound value`() = runTest {
        // Given
        doReturn(
            Result.Success(
                """
                    {
                        "gameType": "Grid",
                        "levels": [
                        {
                            "levelId": 0,
                            "settings": {
                                "recallsBack": 3,
                                "totalRounds": 5,
                                "millisPerRound": 0,
                                "repeatChance": 20
                            }
                    }]}
                """.trimIndent(),
            ),
        ).`when`(spiedLevelRepository).readLevelData(Game.Grid)

        // When
        val settings = spiedLevelRepository.get(LevelData(Game.Grid, 0))

        // Then
        assert(settings == Result.Error(LevelDeserializationError.InvalidRoundTime))
    }

    @Test
    fun `error when loading level from invalid config that has a too small repeatChance value`() = runTest {
        // Given
        doReturn(
            Result.Success(
                """
                    {
                        "gameType": "Grid",
                        "levels": [
                        {
                            "levelId": 0,
                            "settings": {
                                "recallsBack": 3,
                                "totalRounds": 5,
                                "millisPerRound": 1000,
                                "repeatChance": -5
                            }
                    }]}
                """.trimIndent(),
            ),
        ).`when`(spiedLevelRepository).readLevelData(Game.Grid)

        // When
        val settings = spiedLevelRepository.get(LevelData(Game.Grid, 0))

        // Then
        assert(settings == Result.Error(LevelDeserializationError.InvalidRepeatChance))
    }

    @Test
    fun `error when loading level from invalid config that has a too large repeatChance value`() = runTest {
        // Given
        doReturn(
            Result.Success(
                """
                    {
                        "gameType": "Grid",
                        "levels": [
                        {
                            "levelId": 0,
                            "settings": {
                                "recallsBack": 3,
                                "totalRounds": 5,
                                "millisPerRound": 1000,
                                "repeatChance": 101
                            }
                    }]}
                """.trimIndent(),
            ),
        ).`when`(spiedLevelRepository).readLevelData(Game.Grid)

        // When
        val settings = spiedLevelRepository.get(LevelData(Game.Grid, 0))

        // Then
        assert(settings == Result.Error(LevelDeserializationError.InvalidRepeatChance))
    }

    @Test
    fun `error when loading config with duplicate level ids`() = runTest {
        // Given
        doReturn(
            Result.Success(
                """
                    {
                        "gameType": "Grid",
                        "levels": [
                        {
                            "levelId": 0,
                            "settings": {
                                "recallsBack": 3,
                                "totalRounds": 5,
                                "millisPerRound": 1000,
                                "repeatChance": 20
                            }
                        },
                        {
                            "levelId": 0,
                            "settings": {
                                "recallsBack": 3,
                                "totalRounds": 6,
                                "millisPerRound": 1000,
                                "repeatChance": 20
                            }
                    }]}
                """.trimIndent(),
            ),
        ).`when`(spiedLevelRepository).readLevelData(Game.Grid)

        // When
        val settings = spiedLevelRepository.get(LevelData(Game.Grid, 0))

        // Then
        assert(settings == Result.Error(LevelDeserializationError.LevelMismatch))
    }

    @Test
    fun `error when loading config with missing level ids`() = runTest {
        // Given
        doReturn(
            Result.Success(
                """
                    {
                        "gameType": "Grid",
                        "levels": [
                        {
                            "levelId": 0,
                            "settings": {
                                "recallsBack": 3,
                                "totalRounds": 5,
                                "millisPerRound": 1000,
                                "repeatChance": 20
                            }
                        },
                        {
                            "levelId": 2,
                            "settings": {
                                "recallsBack": 3,
                                "totalRounds": 6,
                                "millisPerRound": 1000,
                                "repeatChance": 20
                            }
                    }]}
                """.trimIndent(),
            ),
        ).`when`(spiedLevelRepository).readLevelData(Game.Grid)

        // When
        val settings = spiedLevelRepository.get(LevelData(Game.Grid, 0))

        // Then
        assert(settings == Result.Error(LevelDeserializationError.LevelMismatch))
    }

    @Test
    fun `error when loading config with scrambled level ids`() = runTest {
        // Given
        doReturn(
            Result.Success(
                """
                    {
                        "gameType": "Grid",
                        "levels": [
                        {
                            "levelId": 1,
                            "settings": {
                                "recallsBack": 3,
                                "totalRounds": 5,
                                "millisPerRound": 1000,
                                "repeatChance": 20
                            }
                        },
                        {
                            "levelId": 0,
                            "settings": {
                                "recallsBack": 3,
                                "totalRounds": 6,
                                "millisPerRound": 1000,
                                "repeatChance": 20
                            }
                    }]}
                """.trimIndent(),
            ),
        ).`when`(spiedLevelRepository).readLevelData(Game.Grid)

        // When
        val settings = spiedLevelRepository.get(LevelData(Game.Grid, 0))

        // Then
        assert(settings == Result.Error(LevelDeserializationError.LevelMismatch))
    }
}
