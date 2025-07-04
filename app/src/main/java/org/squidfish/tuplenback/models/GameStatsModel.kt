package org.squidfish.tuplenback.models

import org.squidfish.tuplenback.data.BaseGameStats
import org.squidfish.tuplenback.games.Game
import org.squidfish.tuplenback.games.GameModule

/**
 * Player round stats in a game.
 *
 * @param[correctRecalls] The times a player signaled a mnemonic repeat when there was one.
 * @param[incorrectRecalls] THe times a player signaled a mnemonic repeat when there wasn't one.
 * @param[missedRecalls] The times a mnemonic repeat happened, but the player didn't signal it.
 */
data class GameStatsModel(
    override val gameEndTime: Long = System.currentTimeMillis(),
    override val gameType: Game? = null,
    override val gameModule: GameModule? = null,
    override val difficulty: Int,
    override val correctRecalls: Int = 0,
    override val incorrectRecalls: Int = 0,
    override val missedRecalls: Int = 0,
) : BaseGameStats
