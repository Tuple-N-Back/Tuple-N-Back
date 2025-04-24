package org.squidfish.tuplenback.data

import java.text.SimpleDateFormat
import java.util.Locale
import org.squidfish.tuplenback.games.Game

interface GameStatsEntry {
    val gameEndTime: Long
    val gameType: Game
    val difficulty: Int
    val correctGuesses: Int
    val incorrectGuesses: Int
    val missedGuesses: Int

    fun getEndTimeAsString(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.getDefault()).format(gameEndTime)
    }
}
