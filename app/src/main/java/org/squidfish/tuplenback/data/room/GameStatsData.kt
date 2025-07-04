package org.squidfish.tuplenback.data.room

import androidx.room.Entity
import org.squidfish.tuplenback.games.Game
import org.squidfish.tuplenback.games.GameModule

@Entity(primaryKeys = ["gameEndTime", "gameModule"])
data class GameStatsData(
    // Game Type
    val gameType: Game,
    val gameModule: GameModule,

    // Game Settings
    val difficulty: Int,
    val millisecondsPerRound: Long,
    val mnemonicRepeatChance: Int,

    // Stats
    val correctRecalls: Int,
    val incorrectRecalls: Int,
    val missedRecalls: Int,
    val correctNonRecalls: Int,
    val gameEndTime: Long = System.currentTimeMillis(),
)
