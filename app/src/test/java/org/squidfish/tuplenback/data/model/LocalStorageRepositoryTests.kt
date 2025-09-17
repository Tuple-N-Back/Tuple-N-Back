package org.squidfish.tuplenback.data.model

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.squidfish.tuplenback.data.game.GameRepository
import org.squidfish.tuplenback.data.room.AppDatabase
import org.squidfish.tuplenback.games.Game
import org.squidfish.tuplenback.games.GameModule
import org.squidfish.tuplenback.games.GameSettings
import org.squidfish.tuplenback.models.GameModel
import org.squidfish.tuplenback.models.PlayerPerformanceStats
import org.squidfish.tuplenback.utils.data

@RunWith(RobolectricTestRunner::class)
class LocalStorageRepositoryTests {
    private lateinit var db: AppDatabase
    private lateinit var repository: GameRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        this.db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
        this.repository = GameRepository(this.db)
    }

    @After
    fun closeDb() {
        if (::db.isInitialized) {
            db.close()
        }
    }

    @Test
    fun `insert makes a GameModel retrievable by getRecent`() = runTest {
        // Given
        val gameModel = GameModel(
            gameType = Game.Grid,
            level = 3,
            gameSettings = GameSettings(
                recallsBack = 3,
                totalRounds = 3,
                millisPerRound = 1000,
                repeatChance = 50,
                timerUpdateInterval = 30,
            ),
            playerStats = mapOf(
                GameModule.Grid to PlayerPerformanceStats(
                    correctRecalls = 1,
                    incorrectRecalls = 2,
                    missedRecalls = 2,
                    correctNonRecalls = 8,
                ),
            ),
            gameEndTime = 100,
        )

        // When
        repository.insert(gameModel)

        // Then
        assert(gameModel == repository.getRecent().data)
    }

    @Test
    fun `getRecent retrieves the game that has concluded more recently out of 3 out-of-order inserted GameModels`() =
        runTest {
            // Given
            val gameModelFirst = GameModel(
                gameType = Game.Grid,
                level = 3,
                gameSettings = GameSettings(
                    recallsBack = 3,
                    totalRounds = 3,
                    millisPerRound = 1000,
                    repeatChance = 50,
                    timerUpdateInterval = 30,
                ),
                playerStats = mapOf(
                    GameModule.Grid to PlayerPerformanceStats(
                        correctRecalls = 1,
                        incorrectRecalls = 1,
                        missedRecalls = 1,
                        correctNonRecalls = 0,
                    ),
                ),
                gameEndTime = 100,
            )

            val gameModelMiddle = GameModel(
                gameType = Game.Grid,
                level = 3,
                gameSettings = GameSettings(
                    recallsBack = 3,
                    totalRounds = 3,
                    millisPerRound = 1000,
                    repeatChance = 50,
                    timerUpdateInterval = 30,
                ),
                playerStats = mapOf(
                    GameModule.Grid to PlayerPerformanceStats(
                        correctRecalls = 1,
                        incorrectRecalls = 2,
                        missedRecalls = 3,
                        correctNonRecalls = 4,
                    ),
                ),
                gameEndTime = 200,
            )

            val gameModelLatest = GameModel(
                gameType = Game.Grid,
                level = 3,
                gameSettings = GameSettings(
                    recallsBack = 3,
                    totalRounds = 3,
                    millisPerRound = 1000,
                    repeatChance = 50,
                    timerUpdateInterval = 30,
                ),
                playerStats = mapOf(
                    GameModule.Grid to PlayerPerformanceStats(
                        correctRecalls = 4,
                        incorrectRecalls = 6,
                        missedRecalls = 7,
                        correctNonRecalls = 8,
                    ),
                ),
                gameEndTime = 300,
            )

            // When
            repository.insert(gameModelMiddle)
            repository.insert(gameModelLatest)
            repository.insert(gameModelFirst)

            // Then
            assert(gameModelLatest == repository.getRecent().data)
        }

    @Test
    fun `getRecent returns null when no game models have been inserted`() = runTest {
        assert(repository.getRecent().data == null)
    }

    @Test
    fun `delete makes a game model unretrievable via getRecent`() = runTest {
        // Given
        val gameModel = GameModel(
            gameType = Game.Grid,
            level = 3,
            gameSettings = GameSettings(
                recallsBack = 3,
                totalRounds = 3,
                millisPerRound = 1000,
                repeatChance = 50,
                timerUpdateInterval = 30,
            ),
            playerStats = mapOf(
                GameModule.Grid to PlayerPerformanceStats(
                    correctRecalls = 1,
                    incorrectRecalls = 2,
                    missedRecalls = 2,
                    correctNonRecalls = 8,
                ),
            ),
            gameEndTime = 100,
        )

        repository.insert(gameModel)

        // When
        repository.delete(gameModel)

        // Then
        assert(repository.getRecent().data == null)
    }
}
