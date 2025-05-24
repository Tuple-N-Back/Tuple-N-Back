package org.squidfish.tuplenback.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import org.squidfish.tuplenback.games.Game

@Dao
interface GameStatsDao {
    @Query(
        "SELECT * FROM gamestatsdata" +
            " WHERE gameType = :gameType" +
            " ORDER BY gameEndTime DESC" +
            " LIMIT :amount",
    )
    suspend fun getByGameType(amount: Int, gameType: Game): List<GameStatsData>

    @Query(
        "SELECT * FROM gamestatsdata" +
            " ORDER BY gameEndTime DESC" +
            " LIMIT :amount",
    )
    suspend fun getRecent(amount: Int): List<GameStatsData>

    @Insert
    suspend fun insert(gameStatsData: GameStatsData)

    @Delete
    suspend fun delete(vararg gameStatEntries: GameStatsData)
}
