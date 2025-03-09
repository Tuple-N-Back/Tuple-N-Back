package org.squidfish.tuple_n_back.models

/**
 * Player round stats in a game.
 *
 * @param[correctRecalls] player recalled correctly
 * @param[incorrectRecalls] player recalled incorrectly
 * @param[missedRecalls] player should have recalled, but didn't
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