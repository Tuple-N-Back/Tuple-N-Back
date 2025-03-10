package org.squidfish.tuple_n_back.models

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import org.squidfish.tuple_n_back.NQueue

/**
 * Abstract class for general game behaviour, that is, generating new mnemonics, updating the
 * game's state and keeping track of player statistics.
 *
 * @param[recallsBack] Amount of mnemonics the player has to remember.
 * @property[gameButtonText] How the recall button will be named - should be the game's name
 * @property[queue] Collection containing the recallsBack most recent mnemonics.
 * @property[isRepeat] True if there is a mnemonic repeat, that is, the most recent mnemonic is of
 * the same type than the [recallsBack+1] mnemonic.
 * @property[stats] Player performance statistics. See [GameStats] for details.
 */
abstract class GameEngine(recallsBack: Int) {
    abstract val gameButtonText: String

    // mnemonic queue
    private val queue by mutableStateOf(NQueue<Int>(recallsBack))
    var isRepeat by mutableStateOf(false)

    private var stats = GameStats()

    /**
     * Generate a random game mnemonic i.e. game state.
     * @return integer representing the mnemonic.
     */
    protected abstract fun genNewMnemonic(): Int

    /**
     * Add a new mnemonic to the game.
     */
    fun createNewState(): Int {
        val state = genNewMnemonic()

        // can't have repeats when we haven't reached turn n
        if (queue.isFull()) {
            isRepeat = (state == queue.dequeue())
        }

        queue.enqueue(state)
        return state
    }

    /**
     * Update player game stats.
     *
     * @param[recallGuess] true if the player has guessed
     */
    fun updateStats(recallGuess: Boolean) {
        if (recallGuess && isRepeat) {
            stats.correctRecalls++
        } else if (recallGuess && !isRepeat) {
            stats.incorrectRecalls++
        } else if (!recallGuess && isRepeat) {
            stats.missedRecalls++
        }
    }

    /**
     * Get the most recent mnemonic.
     *
     * @return integer representing the mnemonic
     */
    open fun getRecent() : Int? {
        return queue.getFirst()
    }

    /**
     * Get player stats.
     *
     * @return the player's stats for the game
     */
    fun getStats() : GameStats = stats

    fun resetStats() {
        stats = GameStats()
    }
}