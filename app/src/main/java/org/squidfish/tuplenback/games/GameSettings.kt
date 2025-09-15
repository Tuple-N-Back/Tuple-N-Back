package org.squidfish.tuplenback.games

/**
 * Data class containing game module settings
 *
 * @param[recallsBack] Amount of mnemonics that need to be remembered.
 * @param[totalRounds] Number of rounds - a mnemonic is generated each round.
 * @param[millisPerRound] Time until the next round starts - the player has this much time to
 * remember the current mnemonic event.
 * @param[repeatChance] Chance of a mnemonic event to repeat on any round - the player must signal
 * that a repeat has happened. Repeats can only happen after [recallsBack] rounds have passed.
 * @param[timerUpdateInterval] How often the round timer bar is updated.
 */
data class GameSettings(
    // TODO: totalRounds > recallsBack
    val recallsBack: Int,
    val totalRounds: Int,
    val millisPerRound: Long,
    val repeatChance: Int,

    // TODO: maybe change location, since it doesn't affect game behaviour. On the other hand, the
    //  gameEngines don't care about milliPerRound or totalRounds either
    val timerUpdateInterval: Long = 30, // in millis
)
