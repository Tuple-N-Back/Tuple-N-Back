package org.squidfish.tuplenback

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.squidfish.tuplenback.data.LocalStorageRepository
import org.squidfish.tuplenback.data.room.AppDatabase
import org.squidfish.tuplenback.games.Game
import org.squidfish.tuplenback.games.GameModule
import org.squidfish.tuplenback.models.GameStatsModel

@RunWith(AndroidJUnit4::class)
class LocalStorageRepositoryTests {
    private lateinit var db: AppDatabase
    private lateinit var rep: LocalStorageRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        this.db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
        this.rep = LocalStorageRepository(this.db)
    }

    @After
    fun closeDb() {
        if (::db.isInitialized) {
            db.close()
        }
    }

    @Test
    fun insertSingleGameStats() = runTest {
        // Given
        val gameStatsModel = GameStatsModel(1, Game.Grid, GameModule.Grid, 4, 5, 1, 2)

        // When
        rep.insert(gameStatsModel)

        // Then
        assert(gameStatsModel == rep.getRecent())
    }

    @Test
    fun getRecentmostInsertedGameStatsOutOf3() = runTest {
        // Given
        val gameStatsDataFirst = GameStatsModel(1, Game.Grid, GameModule.Grid, 1, 2, 3, 4)
        val gameStatsDataLatest = GameStatsModel(3, Game.GridPiano, GameModule.Grid, 5, 6, 7, 8)
        val gameStatsDataMiddle = GameStatsModel(2, Game.GridPiano, GameModule.Piano, 1, 2, 3, 4)

        // When
        rep.insert(gameStatsDataMiddle)
        rep.insert(gameStatsDataLatest)
        rep.insert(gameStatsDataFirst)

        // Then
        assert(gameStatsDataLatest == rep.getRecent())
    }

    @Test
    fun retrieveGameStatsWhenNoneAreInserted() = runTest {
        assert(rep.getRecent() == null)
    }

    @Test
    fun deleteSingleGameStats() = runTest {
        // Given
        val gameStatsData = GameStatsModel(1, Game.Grid, GameModule.Grid, 4, 5, 1, 2)
        rep.insert(gameStatsData)

        // When
        rep.delete(gameStatsData)

        // Then
        assert(rep.getRecent() == null)
    }
}
