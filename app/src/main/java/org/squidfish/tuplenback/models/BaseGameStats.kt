package org.squidfish.tuplenback.models

import org.squidfish.tuplenback.games.Game
import org.squidfish.tuplenback.games.GameModule
import java.text.SimpleDateFormat
import java.util.Locale

abstract class BaseGameStats() {
    abstract val gameEndTime: Long
    abstract val gameType: Game
    abstract val gameModule: GameModule
    abstract val difficulty: Int
    abstract val miliPerRound: Long
    abstract val mnemonicRepeatChance: Int
    abstract val correctRecalls: Int
    abstract val incorrectRecalls: Int
    abstract val missedRecalls: Int
    abstract val correctNonRecalls: Int

    fun getEndTimeAsString(): String = SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss z",
        Locale.getDefault(),
    ).format(gameEndTime)
}
