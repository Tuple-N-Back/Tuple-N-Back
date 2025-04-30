package org.squidfish.tuplenback.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.squidfish.tuplenback.data.BaseGameStats
import org.squidfish.tuplenback.games.Game
import org.squidfish.tuplenback.games.GameModule

@Entity(primaryKeys = ["gameEndTime","gameModule"])
data class GameStatsData (
    override val gameEndTime: Long = System.currentTimeMillis(),
    override val gameType: Game,
    override val gameModule: GameModule,
    override val difficulty: Int,
    override val correctRecalls: Int,
    override val incorrectRecalls: Int,
    override val missedRecalls: Int,

    ) : BaseGameStats
