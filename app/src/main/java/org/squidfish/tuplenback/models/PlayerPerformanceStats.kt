package org.squidfish.tuplenback.models

/**
 * Tracks how well a player does for a particular game
 *
 * @param[correctRecalls] The times a player signaled a mnemonic repeat when there was one.
 * @param[incorrectRecalls] The times a player signaled a mnemonic repeat when there wasn't one.
 * @param[missedRecalls] The times a mnemonic repeat happened, but the player didn't signal it.
 * @param[correctNonRecalls] The times a mnemonic repeat didn't happen and the player didn't signal it.
 * @param[rounds] The number of rounds played. All other stats are relative to this one
 */
data class PlayerPerformanceStats(
    val correctRecalls: Int = 0,
    val incorrectRecalls: Int = 0,
    val missedRecalls: Int = 0,
    val correctNonRecalls: Int = 0,
) {
    val rounds: Int
        get() = correctRecalls + incorrectRecalls + missedRecalls + correctNonRecalls
}
