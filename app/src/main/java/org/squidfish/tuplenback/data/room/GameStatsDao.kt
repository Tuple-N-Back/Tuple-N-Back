package org.squidfish.tuplenback.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import org.squidfish.tuplenback.games.Game

@Dao
interface GameStatsDao {
    @Query(
        "SELECT * FROM gamestats" +
        " WHERE gameType = :gameType" +
        " ORDER BY gameEndTime DESC" +
        " LIMIT :amount")
    fun getByGameType(amount: Int, gameType: Game): List<GameStats>

    @Query(
        "SELECT * FROM gamestats" +
            " ORDER BY gameEndTime DESC" +
            " LIMIT :amount")
    fun getRecent(amount: Int): List<GameStats>


    @Insert
    fun insert(gameStats: GameStats)

    @Delete
    fun delete(vararg gameStatEntries: GameStats)
}
