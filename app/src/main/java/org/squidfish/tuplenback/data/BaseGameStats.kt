package org.squidfish.tuplenback.data

import java.text.SimpleDateFormat
import java.util.Locale
import org.squidfish.tuplenback.games.Game
import org.squidfish.tuplenback.games.GameModule

interface BaseGameStats {
    val gameEndTime: Long
    val gameType: Game
    val gameModule: GameModule
    val difficulty: Int
    val correctRecalls: Int
    val incorrectRecalls: Int
    val missedRecalls: Int

    fun getEndTimeAsString(): String = SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss z",
        Locale.getDefault(),
    ).format(gameEndTime)
}
