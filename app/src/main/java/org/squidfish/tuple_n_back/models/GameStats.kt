package org.squidfish.tuple_n_back.models

/**
 * Player round stats in a game.
 *
 * @param[correctRecalls] The times a player signaled a mnemonic repeat when there was one.
 * @param[incorrectRecalls] THe times a player signaled a mnemonic repeat when there wasn't one.
 * @param[missedRecalls] The times a mnemonic repeat happened, but the player didn't signal it.
 */
data class GameStats(
    var correctRecalls: Int = 0,
    var incorrectRecalls: Int = 0,
    var missedRecalls: Int = 0,
) {
    fun resetStats() {
        correctRecalls = 0
        incorrectRecalls = 0
        missedRecalls = 0
    }
}