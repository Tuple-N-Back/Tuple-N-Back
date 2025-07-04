package org.squidfish.tuplenback.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import org.squidfish.tuplenback.games.Game

@Dao
interface GameStatsDao {
    @Query(
        """
        SELECT * FROM gamestatsdata
        WHERE gameType = :gameType
        ORDER BY gameEndTime DESC
        LIMIT :amount
    """,
    )
    suspend fun getByGameType(amount: Int, gameType: Game): List<GameStatsData>

    //@Query("""
    //    SELECT * FROM gamestatsdata
    //    ORDER BY gameEndTime DESC
    //    LIMIT :amount
    //""")asd
    @Query("""
        SELECT * FROM gamestatsdata AS a
        INNER JOIN (
            SELECT DISTINCT gameEndTime FROM gamestatsdata
            ORDER BY gameEndTime DESC
            LIMIT :amount) AS b
            ON  a.gameEndTime = b.gameEndTime
    """)
    suspend fun getRecent(amount: Int): List<GameStatsData>

    @Insert
    suspend fun insertAll(gameStatsData: List<GameStatsData>)

    @Delete
    suspend fun deleteAll(vararg gameStatEntries: List<GameStatsData>)
}
