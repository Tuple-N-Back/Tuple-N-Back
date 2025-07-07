package org.squidfish.tuplenback.data.room

import androidx.room.Entity
import org.squidfish.tuplenback.games.Game
import org.squidfish.tuplenback.games.GameModule

@Entity(primaryKeys = ["gameEndTime", "gameModule"])
data class GameStatsData(
    val gameType: Game,
    val gameModule: GameModule,
    val level: Int,
    val correctRecalls: Int,
    val incorrectRecalls: Int,
    val missedRecalls: Int,
    val correctNonRecalls: Int,
    val gameEndTime: Long = System.currentTimeMillis(),
)
