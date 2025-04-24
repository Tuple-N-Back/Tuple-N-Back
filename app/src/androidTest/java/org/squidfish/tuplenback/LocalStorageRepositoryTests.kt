package org.squidfish.tuplenback

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.squidfish.tuplenback.data.GameStatsEntry
import org.squidfish.tuplenback.data.LocalStorageRepository
import org.squidfish.tuplenback.data.room.AppDatabase
import org.squidfish.tuplenback.data.room.GameStats
import org.squidfish.tuplenback.games.Game

@RunWith(AndroidJUnit4::class)
class LocalStorageRepositoryTests {
    private lateinit var db: AppDatabase
    private lateinit var rep: LocalStorageRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        this.db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).allowMainThreadQueries().build()
        this.rep = LocalStorageRepository(this.db)
    }

    @After
    fun closeDb() {
        if (::db.isInitialized) {
            db.close()
        }
    }

    @Test
    fun insertSingleGameStats() {
        // Given
        val gameStats: GameStatsEntry = GameStats(1, Game.Grid, 4, 5, 1, 2)

        // When
        rep.insert(gameStats)

        // Then
        assert(gameStats == rep.getRecent())
    }

    @Test
    fun getRecentmostInsertedGameStatsOutOf3() {
        // Given
        val gameStatsFirst: GameStatsEntry = GameStats(1, Game.Grid, 1, 2, 3, 4)
        val gameStatsLatest: GameStatsEntry = GameStats(3, Game.Grid, 5, 6, 7, 8)
        val gameStatsMiddle: GameStatsEntry = GameStats(2, Game.GridPiano, 1, 2, 3, 4)

        // When
        rep.insert(gameStatsMiddle)
        rep.insert(gameStatsLatest)
        rep.insert(gameStatsFirst)

        // Then
        assert(gameStatsLatest == rep.getRecent())
    }

    @Test
    fun retrieveGameStatsWhenNoneAreInserted() {
        assert(rep.getRecent() == null)
    }

    @Test
    fun deleteSingleGameStats() {
        // Given
        val gameStats: GameStatsEntry = GameStats(1, Game.Grid, 4, 5, 1, 2)
        rep.insert(gameStats)

        // When
        rep.delete(gameStats)

        // Then
        assert(rep.getRecent() == null)
    }
}
