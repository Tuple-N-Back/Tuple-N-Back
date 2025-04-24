package org.squidfish.tuplenback.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.squidfish.tuplenback.data.GameStatsEntry
import org.squidfish.tuplenback.games.Game

@Entity
data class GameStats (
    @PrimaryKey override val gameEndTime: Long = System.currentTimeMillis(),
    override val gameType: Game,
    override val difficulty: Int,
    override val correctGuesses: Int,
    override val incorrectGuesses: Int,
    override val missedGuesses: Int,
) : GameStatsEntry
